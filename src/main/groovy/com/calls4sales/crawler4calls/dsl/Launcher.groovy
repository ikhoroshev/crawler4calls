package com.calls4sales.crawler4calls.dsl

import edu.uci.ics.crawler4j.crawler.CrawlController
import edu.uci.ics.crawler4j.fetcher.PageFetcher
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.streaming.SXSSFSheet
import org.apache.poi.xssf.streaming.SXSSFWorkbook

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

/**
 * Created by Igor on 08.02.2017.
 */
class Launcher {
    public static void main(String[] args) {
        InputStream configStream = System.in
        if (args.length == 1) {
            final Path configFile = Paths.get(args[0])
            if (Files.exists(configFile)) {
                if (Files.isReadable(configFile)) {
                    configStream = Files.newInputStream(configFile, StandardOpenOption.READ)
                } else {
                    println("Can't read file ${args[0]}")
                    usage()
                }
            } else {
                println("File ${args[0]} does not exist")
                usage()
            }
        }
        Scanner scan = new Scanner(configStream);
        StringBuilder configStr = new StringBuilder()
        while(scan.hasNext()) {
            configStr.append(scan.nextLine()).append(System.lineSeparator())
        }

        Config.instance.initialize(configStr.toString())
        PageFetcher pageFetcher = new PageFetcher(Config.instance.crawlConfig);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(Config.instance.crawlConfig, pageFetcher, robotstxtServer);
        controller.addSeed(Config.instance.startURL)
        controller.start(Crawler.class, Config.instance.numberOfThreads)
        output();
    }

    static void output() {
        SXSSFWorkbook wb = new SXSSFWorkbook(100); // keep 100 rows in memory, exceeding rows will be flushed to disk
        SXSSFSheet sh = wb.createSheet();
        Row row = sh.createRow(0);
        List<ColumnConfig> columnConfigs = Config.instance.columnConfigs;
        for (int cellnum = 0; cellnum < columnConfigs.size(); cellnum++) {
            Cell cell = row.createCell(cellnum);
            cell.setCellValue(columnConfigs.getAt(cellnum).name);
        }
        List<List<String>> data = Config.instance.dataCollector;
        for(int rownum = 1; rownum <= data.size(); rownum++){
            row = sh.createRow(rownum);
            String[] rowData = data.get(rownum - 1);
            for(int cellnum = 0; cellnum < rowData.length; cellnum++){
                Cell cell = row.createCell(cellnum);
                String value = rowData[cellnum];
                String cellValue = "";
                if (value != null) {
                    cellValue = value.length() < 32767 ? value : value.substring(0, 32766);
                }
                cell.setCellValue(cellValue);
            }

        }

        FileOutputStream out = new FileOutputStream(Config.instance.crawlConfig.crawlStorageFolder + "/result.xlsx");
        wb.write(out);
        out.close();

        // dispose of temporary files backing this workbook on disk
        wb.dispose();
    }

    static def usage() {
        System.exit(-1)
    }
}
