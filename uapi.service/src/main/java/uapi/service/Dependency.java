/*
 * Copyright (C) 2017. The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.service;

import uapi.common.ArgumentChecker;
import uapi.common.StringHelper;

/**
 * Represent dependency information of service
 */
public class Dependency {

    private final QualifiedServiceId _qSvcId;
    private final Class<?> _svcType;
    private boolean _single = true;
    private boolean _optional = false;

    public Dependency(String qualifiedServiceIdString, Class<?> serviceType) {
        this(QualifiedServiceId.splitTo(qualifiedServiceIdString), serviceType);
    }

    public Dependency(QualifiedServiceId qualifiedServiceId, Class<?> serviceType) {
        this(qualifiedServiceId, serviceType, true, false);
    }

    public Dependency(String qualifiedServiceIdString, Class<?> serviceType, boolean single, boolean optional) {
        this(QualifiedServiceId.splitTo(qualifiedServiceIdString), serviceType, single, optional);
    }

    public Dependency(QualifiedServiceId qualifiedServiceId, Class<?> serviceType, boolean single, boolean optional) {
        ArgumentChecker.required(qualifiedServiceId, "qualifiedServiceIdString");
        ArgumentChecker.required(serviceType, "serviceType");
        this._qSvcId = qualifiedServiceId;
        this._svcType = serviceType;
        this._single = single;
        this._optional = optional;
    }

    public QualifiedServiceId getServiceId() {
        return this._qSvcId;
    }

    public Class<?> getServiceType() {
        return _svcType;
    }

    public void setSingle(boolean single) {
        this._single = single;
    }

    public boolean isSingle() {
        return this._single;
    }

    public void setOptional(boolean optional) {
        this._optional = optional;
    }

    public boolean isOptional() {
        return this._optional;
    }

    @Override
    public int hashCode() {
        return getServiceId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (! (obj instanceof Dependency)) {
            return false;
        }
        Dependency other = (Dependency) obj;
        return getServiceId().equals(other.getServiceId());
    }

    @Override
    public String toString() {
        return StringHelper.makeString("Dependency[{}]", this._qSvcId);
    }
}
