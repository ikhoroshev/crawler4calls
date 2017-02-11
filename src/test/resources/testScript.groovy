startURL "https://omsk.hh.ru/search/vacancy?no_magic=true&items_on_page=100&area=113&enable_snippets=true&text=%D0%A2%D0%B5%D0%BB%D0%B5%D0%BC%D0%B0%D1%80%D0%BA%D0%B5%D1%82%D0%BE%D0%BB%D0%BE%D0%B3&clusters=true&search_field=name&page=0"
shouldVisit { url ->
    (url.contains("https://omsk.hh.ru/search/vacancy?") && url.contains("text=Телемаркетолог")) || url.contains("https://m.hh.ru/vacancies?area=") || (url.contains("https://m.hh.ru/vacancy/") && url.matches("https://m.hh.ru/vacancy/\\d{8}"))
}
shouldParse { page, url ->
    url.matches("https://m.hh.ru/vacancy/\\d{8}")
}
column {
    name  'Наименование вакансии'
    xpath "//h1[@data-qa='vacancy-title']/text()"
}
column {
    name  'Телефон'
    xpath '//div[@class=\'vacancy-contacts-contact__phone\']/a[1]/@href'
    postprocessor { value ->
        //value.substring(value.length() - 11, value.length())
        value.replace('\\D', '')
    }
}
column {
    name  'Зарплата'
    xpath "//div[@data-qa='vacancy-salary']/text()"
}
column {
    name  'URL'
    value 'url'
}
column {
    name  'Описание вакансии'
    xpath "//div[@class='vacancy__description usergenerate']"
    visitor { tagNode ->
        def result = new StringBuilder()
        tagNode.traverse(new TagNodeVisitor() {
            public boolean visit(TagNode parentNode, HtmlNode htmlNode) {
                if (htmlNode instanceof ContentNode) {
                    result.append(((ContentNode) htmlNode).getContent())
                            .append(System.lineSeparator())
                }
                return true
            }
        })
        result.toString();
    }
}
storageFolder "d:/tmp/crawler"
followRedirects true
numberOfThreads 11
printConfig()
