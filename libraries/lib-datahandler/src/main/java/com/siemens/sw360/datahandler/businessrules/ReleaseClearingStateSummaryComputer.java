/*
 * Copyright Siemens AG, 2013-2015. Part of the SW360 Portal Project.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License Version 2.0 as published by the
 * Free Software Foundation with classpath exception.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License version 2.0 for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (please see the COPYING file); if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */
package com.siemens.sw360.datahandler.businessrules;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.siemens.sw360.datahandler.thrift.components.ClearingState;
import com.siemens.sw360.datahandler.thrift.components.FossologyStatus;
import com.siemens.sw360.datahandler.thrift.components.Release;
import com.siemens.sw360.datahandler.thrift.components.ReleaseClearingStateSummary;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Maps.filterKeys;
import static com.siemens.sw360.datahandler.common.CommonUtils.nullToEmptyMap;

/**
 * @author daniele.fognini@tngtech.com
 */
public class ReleaseClearingStateSummaryComputer {
    public static ReleaseClearingStateSummary computeReleaseClearingStateSummary(List<Release> releases, String clearingTeam) {
        ReleaseClearingStateSummary summary = new ReleaseClearingStateSummary(0, 0, 0, 0);

        if (releases == null) {
            return summary;
        }

        for (Release release : releases) {
            Map<String, FossologyStatus> fossologyStatuses = nullToEmptyMap(release.getClearingTeamToFossologyStatus());

            ViewedState globalState = getGlobalState(release.getClearingState());
            ViewedState myTeamState = getStateOfFossology(fossologyStatuses.get(clearingTeam));
            ViewedState otherTeamState = getBestStateOfFossologyForOtherTeams(clearingTeam, fossologyStatuses);

            addReleaseWithStates(summary, globalState, myTeamState, otherTeamState);
        }

        return summary;
    }

    static Void addReleaseWithStates(ReleaseClearingStateSummary summary, ViewedState globalState, ViewedState myTeamState, ViewedState otherTeamState) {
        if (globalState.present()) { // if the release has its clearing state field set use it and ignore the rest
            return addReleaseState(summary, globalState);
        }
        if (myTeamState.present()) { // if my team has something to say ignore the rest
            return addReleaseStateTeamDependent(summary, myTeamState, ViewedState.CLEARING_MY_TEAM);
        }
        if (otherTeamState.present()) { // now consider other teams
            return addReleaseStateTeamDependent(summary, otherTeamState, ViewedState.CLEARING);
        }
        return addReleaseState(summary, ViewedState.NEW); // nobody said anything: it is new
    }

    static ViewedState getGlobalState(ClearingState clearingState) {
        if (clearingState != null) {
            switch (clearingState) {
                case NEW_CLEARING:
                    return ViewedState.NONE; // the default state NEW is like having no global state
                case SENT_TO_FOSSOLOGY:
                case UNDER_CLEARING:
                    return ViewedState.CLEARING;
                case REPORT_AVAILABLE:
                    return ViewedState.REPORT_AVAILABLE;
            }
        }
        return ViewedState.NONE;
    }

    static ViewedState getStateOfFossology(FossologyStatus fossologyStatus) {
        if (fossologyStatus != null) {
            switch (fossologyStatus) {
                case NOT_SENT:
                    return ViewedState.NEW;
                case OPEN:
                case SCANNING:
                case IN_PROGRESS:
                case SENT:
                    return ViewedState.CLEARING;
                case CLOSED:
                case REPORT_AVAILABLE:
                    return ViewedState.REPORT_AVAILABLE;
                case CONNECTION_FAILED:
                case ERROR:
                case NON_EXISTENT:
                case INACCESSIBLE:
                case REJECTED: // ???
                default:
                    break;
            }
        }
        return ViewedState.NONE;
    }

    static Void addReleaseState(ReleaseClearingStateSummary summary, ViewedState state) {
        if (state == null || !state.present()) {
            throwBadState(state);
        }
        switch (state) {
            case NONE:
                throwBadState(state);
            case NEW:
                summary.newRelease++;
                break;
            case CLEARING:
                summary.underClearing++;
                break;
            case CLEARING_MY_TEAM:
                summary.underClearingByProjectTeam++;
                break;
            case REPORT_AVAILABLE:
                summary.reportAvailable++;
                break;
            default:
                break;
        }
        return null;
    }

    static Void addReleaseStateTeamDependent(ReleaseClearingStateSummary summary, ViewedState state, ViewedState stateWhenCleared) {
        if (state == null || !state.present()) {
            throwBadState(state);
        }
        switch (state) {
            case NONE:
                return throwBadState(state);
            case CLEARING:
            case CLEARING_MY_TEAM:
                return addReleaseState(summary, stateWhenCleared);
            case NEW:
            case REPORT_AVAILABLE:
            default:
                return addReleaseState(summary, state);
        }
    }

    static ViewedState getBestStateOfFossologyForOtherTeams(String clearingTeam, Map<String, FossologyStatus> fossologyStatuses) {
        Collection<FossologyStatus> fossologyStatusesOfOtherTeams =
                filterKeys(fossologyStatuses, not(equalTo(clearingTeam))).values();

        Collection<ViewedState> otherTeamsStates = FluentIterable.from(fossologyStatusesOfOtherTeams)
                .transform(functionGetStateOfFossology()).toList();

        return otherTeamsStates.isEmpty() ? ViewedState.NONE : Collections.max(otherTeamsStates);
    }

    static Function<FossologyStatus, ViewedState> functionGetStateOfFossology() {
        return new Function<FossologyStatus, ViewedState>() {
            @Override
            public ViewedState apply(FossologyStatus fossologyStatus) {
                return getStateOfFossology(fossologyStatus);
            }
        };
    }

    static Void throwBadState(ViewedState state) {
        throw new IllegalStateException("down here the state must be present, but it was " + state);
    }

    enum ViewedState {
        NONE(false), NEW, CLEARING, CLEARING_MY_TEAM, REPORT_AVAILABLE;

        private boolean present;

        ViewedState(boolean present) {
            this.present = present;
        }

        ViewedState() {
            this(true);
        }

        private boolean present() {
            return present;
        }
    }
}
