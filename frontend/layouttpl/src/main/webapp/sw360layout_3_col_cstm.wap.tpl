<!-- 
  Copyright Siemens AG, 2013-2014. Part of the SW360 Portal Project.
 
  This program is free software; you can redistribute it and/or modify it under
  the terms of the GNU General Public License Version 2.0 as published by the
  Free Software Foundation with classpath exception.
 
  This program is distributed in the hope that it will be useful, but WITHOUT
  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
  FOR A PARTICULAR PURPOSE. See the GNU General Public License version 2.0 for
  more details.
 
  You should have received a copy of the GNU General Public License along with
  this program (please see the COPYING file); if not, write to the Free
  Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
  02110-1301, USA.
 
  Authors: nunifar.ms@siemens.com, puspa.panda@siemens.com, 
    cedric.bodet.ext@siemens.com, johannes.najjar.ext@siemens.com
  
  Description: Page layout file for custom 3-column layout
-->
<style type="text/css">
#layout-column_column-3 {
	border-left: 2px solid #cccccc;
	padding-left:25px;
	margin-left:20px;
}
</style>

<div class="sw360layout_3_col_cstm" id="main-content" role="main">
   <div class="portlet-layout row-fluid">
      <div class="portlet-column portlet-column-first span5" id="column-1">
         $processor.processColumn("column-1", "portlet-column-content portlet-column-content-first")
      </div>

      <div class="portlet-column span5" id="column-2">
         $processor.processColumn("column-2", "portlet-column-content")
      </div>

      <div class="portlet-column portlet-column-last span2" id="column-3">
         $processor.processColumn("column-3", "portlet-column-content portlet-column-content-last")
      </div>
   </div>
</div>