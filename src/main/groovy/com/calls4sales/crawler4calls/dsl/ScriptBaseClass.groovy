package com.calls4sales.crawler4calls.dsl
/**
 * Created by Igor on 07.02.2017.
 */
abstract class ScriptBaseClass extends Script{
    Config config = Config.instance
    void printConfig(){println(config.toString())}
    void storageFolder(String folder) {config.crawlConfig.crawlStorageFolder = folder}
    void followRedirects(boolean b) {config.crawlConfig.followRedirects = b}
    void numberOfThreads(int i) {config.numberOfThreads = i}
    void startURL(String url) {config.startURL = url}
    def column(Closure cl) {
        def column = new ColumnConfig()
        def code = cl.rehydrate(column, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        config.columnConfigs.add(column)
    }
    def shouldVisit(Closure cl) {
        config.shouldVisit = cl
    }
    def shouldParse(Closure cl) {
        config.shouldParse = cl
    }
}
