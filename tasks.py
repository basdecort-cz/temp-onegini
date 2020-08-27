import boto3
import os
from invoke import run, task
from mimetypes import MimeTypes
from xml.etree import ElementTree

plugin = ElementTree.parse('plugin.xml')
version = plugin.getroot().attrib['version']


def get_mime_type(file):
    mime = MimeTypes()
    mime_type = mime.guess_type(file)
    return mime_type


def upload_folder_to_s3(source, destination, bucket):
    s3 = boto3.resource('s3')
    for root, dirs, files in os.walk(source):
        for filename in files:

            local_path = os.path.join(root, filename)
            relative_path = os.path.relpath(local_path, source)
            s3_path = os.path.join(destination, relative_path)

            data = open(local_path, 'rb')
            mime_type = get_mime_type(local_path)

            if mime_type[0] is not None:
                print("Uploading %s with MIME type %s..." % (s3_path, mime_type[0]))
                s3.Bucket(bucket).put_object(Key=s3_path, Body=data, ContentType=mime_type[0])
            else:
                print("Uploading %s without MIME tpye..." % s3_path)
                s3.Bucket(bucket).put_object(Key=s3_path, Body=data)

            data.close()


@task
def documentation(ctx):
    if 'SNAPSHOT' not in version:
        run('cd docs && npm run clean'.encode('ascii', 'ignore'))
        run('cd docs && npm install'.encode('ascii', 'ignore'))
        run('./docs/node_modules/.bin/gitbook build docs/public')
        upload_folder_to_s3('docs/public/_book/', 'msp/cordova-plugin/%s' % version, 'onegini-documentation')