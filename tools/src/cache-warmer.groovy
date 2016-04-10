#!/usr/bin/env groovy
@GrabConfig(systemClassLoader = true)
@Grab(group = 'mysql', module = 'mysql-connector-java', version = '5.1.36')
@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.7.1')
@GrabExclude(group = 'xerces', module = 'xercesImpl')

import groovy.sql.Sql

class cacheWarmer {

    final Sql site;

    static void main(args) {
        Sql site = connect('localhost', 'ruranobe', 'ruranobe', '123');
        def migrationTool = new cacheWarmer(site);
        println("Update volumes texts")
        migrationTool.updateVolumeTexts()
        println("Update converter caches")
        migrationTool.updateConverterCaches()
        println("Warmed up")
    }

    static Sql connect(String host, String db, String user, String password) {
        return Sql.newInstance("jdbc:mysql://${host}/${db}?useUnicode=true&characterEncoding=UTF-8", user, password, 'com.mysql.jdbc.Driver')
    }

    cacheWarmer(Sql site) {
        this.site = site
    }

    def updateVolumeTexts() {
        site.eachRow("""
SELECT
    vol.volume_id AS volume_id, vol.url AS url, vol.name_title AS name_title
FROM
    volumes vol
ORDER BY
    vol.project_id, vol.sequence_number;
""") { volume ->
            println(volume.name_title)
            "http://ruranobe.ru/r/${volume.url}/text".toURL().text
        }
    }

    def download(String address, String filename)
    {
        def file = new FileOutputStream(filename)
        def out = new BufferedOutputStream(file)
        out << new URL(address).openStream()
        out.close()
    }

    def updateConverterCaches() {
        site.eachRow("""
SELECT
    vol.volume_id AS volume_id, vol.url AS url, vol.name_title AS name_title, vol.name_file AS name_file
FROM
    volumes vol
ORDER BY
    vol.project_id, vol.sequence_number;
""") { volume ->
            println(volume.name_title)
            try {
                download("http://ruranobe.ru/d/fb2/${volume.url}".toString(), volume.name_file + ".fb2")
            } catch (IOException e) {
                println e.getMessage()
            }
            try {
                download("http://ruranobe.ru/d/fb2/${volume.url}?pic=0".toString(), volume.name_file + "_nopic.fb2")
            } catch (IOException e) {
                println e.getMessage()
            }
            try {
                download("http://ruranobe.ru/d/docx/${volume.url}".toString(), volume.name_file + ".docx")
            } catch (IOException e) {
                println e.getMessage()
            }
            try {
                download("http://ruranobe.ru/d/epub/${volume.url}".toString(), volume.name_file + ".epub")
            } catch (IOException e) {
                println e.getMessage()
            }
        }
    }
}
