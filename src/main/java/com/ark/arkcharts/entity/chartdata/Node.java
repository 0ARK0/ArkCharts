package com.ark.arkcharts.entity.chartdata;

import java.util.List;

/**
 * @author Ark
 * @date 2020/05/17 7:59
 * 思维导图的节点类
 */
public class Node {
    private String pid;
    private String name;
    private List<Node> children = null;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }
}
