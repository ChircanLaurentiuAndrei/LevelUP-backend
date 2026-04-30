package com.levelup.backend.enums;

public sealed interface TaskStatus permits TaskStatus.Pending, TaskStatus.Verifying, TaskStatus.Completed, TaskStatus.Failed {
    record Pending() implements TaskStatus {}
    record Verifying() implements TaskStatus {}
    record Completed() implements TaskStatus {}
    record Failed() implements TaskStatus {}
    
    TaskStatus PENDING = new Pending();
    TaskStatus VERIFYING = new Verifying();
    TaskStatus COMPLETED = new Completed();
    TaskStatus FAILED = new Failed();
}
