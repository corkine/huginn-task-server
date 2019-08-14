package com.mazhangjing.fast.task_server.service;

import java.util.Objects;

public class TaskSkeleton {
    String group;
    String data;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskSkeleton that = (TaskSkeleton) o;
        return Objects.equals(group, that.group) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, data);
    }

    @Override
    public String toString() {
        return "TaskSkeleton{" +
                "group='" + group + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public TaskSkeleton(String group, String data) {
        this.group = group;
        this.data = data;
    }

    public TaskSkeleton() {
    }
}
