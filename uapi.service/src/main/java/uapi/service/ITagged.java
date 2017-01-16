/*
 * Copyright (C) 2017. The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service;

/**
 * A service with one or more tag
 */
public interface ITagged {

    String METHOD_GETTAGS   = "getTags";

    String[] getTags();
}
