from fabric.api import local
import re
import sys
import os
import shutil

scala_version_file = 'project/Version.scala'
gradle_version_file = 'aeromock-dsl/gradle.properties'
gradle_tmp_file = 'tmp_gradle_version'
scala_tmp_file = 'tmp_scala_version'

def release(next_version):
    # remove SNAPSHOT scala
    to_release_version_scala()
    # remove SNAPSHOT Gradle
    to_release_version_gradle()
    release_version = get_release_version()

    # build aeromock-dsl
    local('./aeromock-dsl/gradlew -p aeromock-dsl install uploadArchives')

    # build aeromock
    local('sbt publishSigned')

    # commit & tag release version
    commit('pre tag commit \'%s\'.' % release_version)
    tag_name = 'v%s' % release_version
    local('git tag %s' % tag_name)

    # to snapshot version
    to_snapshot_version_scala(next_version)
    to_snapshot_version_gradle(next_version)

    # commit snapshot version
    commit('- new version commit: \'%s-SNAPSHOT\'.' % next_version)

    # push
    local('git push origin master')
    local('git push origin %s' % tag_name)

def to_release_version_scala():

    p = re.compile('"\s*([0-9A-Z\-\.]*)-SNAPSHOT\s*"')

    try:
        version_file = open(scala_version_file, 'r')
        write_file = open(scala_tmp_file, 'w')
        for line in version_file:
            result = p.search(line)
            if result:
                write_file.write('  val aeromock = "%s"\n' % result.group(1))
            else:
                write_file.write(line)
    finally:
        version_file.close()
        write_file.close()

    os.remove(scala_version_file)
    shutil.move(scala_tmp_file, scala_version_file)

def to_release_version_gradle():

    p = re.compile('\s*version\s*=\s*([0-9A-Z\-\.]*)-SNAPSHOT\s*')

    try:
        version_file = open(gradle_version_file, 'r')
        write_file = open(gradle_tmp_file, 'w')
        for line in version_file:
            result = p.search(line)
            if result:
                write_file.write('version=%s\n' % result.group(1))
            else:
                write_file.write(line)
    finally:
        version_file.close()
        write_file.close()

    os.remove(gradle_version_file)
    shutil.move(gradle_tmp_file, gradle_version_file)

def to_snapshot_version_scala(next_version):
    p = re.compile('"\s*[0-9A-Z\-\.]*\s*"')

    try:
        version_file = open(scala_version_file, 'r')
        write_file = open(scala_tmp_file, 'w')
        for line in version_file:
            result = p.search(line)
            if result:
                write_file.write('  val aeromock = "%s-SNAPSHOT"\n' % next_version)
            else:
                write_file.write(line)
    finally:
        version_file.close()
        write_file.close()

    os.remove(scala_version_file)
    shutil.move(scala_tmp_file, scala_version_file)

def to_snapshot_version_gradle(next_version):

    p = re.compile('\s*version\s*=\s*[0-9A-Z\-\.]+\s*')

    try:
        version_file = open(gradle_version_file, 'r')
        write_file = open(gradle_tmp_file, 'w')
        for line in version_file:
            result = p.search(line)
            if result:
                write_file.write('version=%s-SNAPSHOT\n' % next_version)
            else:
                write_file.write(line)
    finally:
        version_file.close()
        write_file.close()

    os.remove(gradle_version_file)
    shutil.move(gradle_tmp_file, gradle_version_file)


def get_release_version():

    p = re.compile('\s*version\s*=\s*([0-9A-Z\-\.]*)\s*')

    try:
        version_file = open('aeromock-dsl/gradle.properties', 'r')
        version = version_file.read()
        result = p.search(version)
        if result:
            return result.group(1)
        else:
            raise SystemError('cannot get release version!')
    finally:
        version_file.close()

def finish_release(release_version, next_version):
    # add files
    local('git add -A')
    # commit release version
    local('git commit -m "[Aeromock Release Task] pre tag commit \'%s\'."' % release_version)
    # create tag
    local('git tag v%s' % release_version)

def commit(message):
    # add files
    local('git add -A')
    # commit
    local('git commit -m "[Aeromock Release Task] %s"' % message)
