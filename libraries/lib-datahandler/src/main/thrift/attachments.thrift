/*
 * Copyright Siemens AG, 2014-2015. Part of the SW360 Portal Project.
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
include "users.thrift"
include "sw360.thrift"

namespace java com.siemens.sw360.datahandler.thrift.attachments
namespace php sw360.thrift.attachments

typedef sw360.RequestStatus RequestStatus
typedef sw360.RequestSummary RequestSummary
typedef users.User User

enum AttachmentType {
    DOCUMENT = 0,
    SOURCE = 1,
    DESIGN = 2,
    REQUIREMENT = 3,
    CLEARING_REPORT = 4,
    COMPONENT_LICENSE_INFO_XML = 5,
    COMPONENT_LICENSE_INFO_COMBINED = 6,
    SCAN_RESULT_REPORT = 7,
    SCAN_RESULT_REPORT_XML = 8,
    SOURCE_SELF = 9,
    BINARY = 10,
    BINARY_SELF = 11,
    DECISION_REPORT = 12,
    LEGAL_EVALUATION = 13,
    LICENSE_AGREEMENT = 14,
    SCREENSHOT = 15,
    OTHER = 16
}

enum CheckStatus {
    NOTCHECKED = 0,
    ACCEPTED = 1,
    REJECTED = 2
}

struct Attachment {
    // TODO mcj check for tests for added fields on 20151021
    1: required string attachmentContentId,
    5: required string filename,
    6: optional string sha1,

    10: optional AttachmentType attachmentType,

    11: optional string createdBy, // should be e-mail
    12: optional string createdTeam, // team name
    13: optional string createdComment,
    14: optional string createdOn,
    15: optional string checkedBy, // should be e-mail
    16: optional string checkedTeam, // team name
    17: optional string checkedComment, // team name
    18: optional string checkedOn, // strange to have string, but thrift?

    20: optional set<string> uploadHistory, // just for importing data by now
    21: optional CheckStatus checkStatus; // simple status of checks
}

struct AttachmentContent {
    1: optional string id,
    2: optional string revision,
    3: optional string type = "attachment",

    10: optional bool onlyRemote;
    11: optional string remoteUrl,

    20: required string filename,
    21: optional string contentType,
    22: optional string partsCount,
}

struct FilledAttachment {
    1: required Attachment attachment,
    2: required AttachmentContent attachmentContent,
}

struct DatabaseAddress {
    1: required string url,
    2: required string dbName
}

service AttachmentService {

    // Returns the URL and database name where to upload the attachment
    DatabaseAddress getDatabaseAddress();

    // Make a new attachment object
    AttachmentContent makeAttachmentContent(1:AttachmentContent attachmentContent);
    list<AttachmentContent> makeAttachmentContents(1:list<AttachmentContent> attachmentContents);

    // Get one or more attachment details
    AttachmentContent getAttachmentContent(1:string id);

    // Update or delete attachment
    oneway void updateAttachmentContent(1:AttachmentContent attachment);

    RequestSummary bulkDelete(1: list<string> ids);

    RequestStatus deleteAttachmentContent(1: string attachmentId);

    RequestSummary vacuumAttachmentDB(1: User user, 2: set<string > usedIds);

    string getSha1FromAttachmentContentId(1: string attachmentContentId);
}
