package com.calls4sales.crawler4calls.dsl

/**
 * Created by Igor on 07.02.2017.
 */
class ColumnConfig {
    String name
    String xpath
    String value
    Closure visitor
    Closure postprocessor

    void name(String name) {this.name = name}
    void xpath(String xpath) {this.xpath = xpath}
    void value(String value) {this.value =value}
    void visitor(Closure cl) {this.visitor = cl}
    void postprocessor(Closure cl) {this.postprocessor = cl}

    @Override
    public String toString() {
        return "ColumnConfig{" +
                "name='" + name + '\'' +
                ", xpath='" + xpath + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
