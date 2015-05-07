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

package org.oclc.archivegraph.model;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Description:
 * User: jamiesoh
 * Date: 5/5/15
 * Time: 2:12 PM
 * &copy;2013 OCLC Data Architecture Group
 */
public class CreateRequest2Test {
    @Test
    public void testRead() {
        try {
            List<String> lines = IOUtils.readLines(new InputStreamReader(this.getClass().getResourceAsStream("/load-fmt-2.txt")));
            Collection<CreateRequest2> noKeys = new ArrayList<CreateRequest2>();
            for (String line : lines) {
                CreateRequest2 r2 = new CreateRequest2(line);
                Validate.notEmpty(r2.getIndex());
                Validate.notEmpty(r2.getType());
                Validate.isTrue(r2.getDoc().startsWith("{"));
                Validate.isTrue(r2.getDoc().endsWith("}"));
                if (r2.needsId()) {
                    noKeys.add(r2);
                }
            }
            assertThat(noKeys.size(), equalTo(1));
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
