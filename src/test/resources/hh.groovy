//https://spb.hh.ru/search/vacancy?text=%22%D0%B4%D0%B8%D1%80%D0%B5%D0%BA%D1%82%D0%BE%D1%80+%D0%BF%D0%BE+%D0%BF%D1%80%D0%BE%D0%B4%D0%B0%D0%B6%D0%B0%D0%BC%22+AND+%28%22%D1%85%D0%BE%D0%BB%D0%BE%D0%B4%D0%BD%D1%8B%D0%B5%22+OR+%22%D0%B0%D0%BA%D1%82%D0%B8%D0%B2%D0%BD%D1%8B%D0%B5+%D0%BF%D1%80%D0%BE%D0%B4%D0%B0%D0%B6%D0%B8%22%29&items_on_page=100&clusters=true&enable_snippets=true&page=1
startURL "https://spb.hh.ru/search/vacancy?items_on_page=100&text=%22%D0%B4%D0%B8%D1%80%D0%B5%D0%BA%D1%82%D0%BE%D1%80+%D0%BF%D0%BE+%D0%BF%D1%80%D0%BE%D0%B4%D0%B0%D0%B6%D0%B0%D0%BC%22+AND+%28%22%D1%85%D0%BE%D0%BB%D0%BE%D0%B4%D0%BD%D1%8B%D0%B5%22+OR+%22%D0%B0%D0%BA%D1%82%D0%B8%D0%B2%D0%BD%D1%8B%D0%B5+%D0%BF%D1%80%D0%BE%D0%B4%D0%B0%D0%B6%D0%B8%22%29&only_with_salary=false&enable_snippets=true&clusters=true&page=0"
shouldVisit { url ->
    (url.contains("https://spb.hh.ru/search/vacancy?") && url.contains("items_on_page"))|| url.contains("https://m.hh.ru/vacancies?") || (url.contains("https://m.hh.ru/vacancy/") && url.matches("https://m.hh.ru/vacancy/\\d{8}"))
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
