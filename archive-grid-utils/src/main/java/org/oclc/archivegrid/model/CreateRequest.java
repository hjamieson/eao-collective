/**
 * *************************************************************************************************************
 * <p/>
 * Copyright (c) 2014 OCLC, Inc. All Rights Reserved.
 * <p/>
 * OCLC proprietary information: the enclosed materials contain
 * proprietary information of OCLC, Inc. and shall not be disclosed in whole or in
 * any part to any third party or used by any person for any purpose, without written
 * consent of OCLC, Inc.  Duplication of any portion of these materials shall include this notice.
 * <p/>
 * ****************************************************************************************************************
 */

package org.oclc.archivegrid.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * User: jamiesoh
 * Date: 4/23/15
 * Time: 1:24 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class CreateRequest {
    private Map<String, Object> command = new HashMap<>();
    private Map<String, Object> data = new HashMap<>();

    public CreateRequest() {
    }

    public Map<String, Object> getCommand() {
        return command;
    }

    public void setCommand(Map<String, Object> command) {
        this.command = command;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getIndex() {
        Map<String, Object> create = (Map<String, Object>) command.get("create");
        return (String) create.get("_index");
    }

    public String getType() {
        Map<String, Object> create = (Map<String, Object>) command.get("create");
        return (String) create.get("_type");
    }

    public String getId() {
        Map<String, Object> create = (Map<String, Object>) command.get("create");
        return (String) create.get("_id");
    }
}
