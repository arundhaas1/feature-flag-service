package com.flag.featureflagservice.model;

public enum Role {
    ADMIN,      // manage applications, flags, users
    EDITOR,     // toggle flags
    VIEWER      // read-only
}
