/**
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

import com.virtualdogbert.GroovyConfigWriter
import org.yaml.snakeyaml.Yaml

description("Converts a yml config, to a groovy config") {
    usage "yml-to-groovy-config [ymlFile] [optional outputFile] [optional indent] [optional csv list values to escape]"
    argument name: 'ymlFile', description: 'The yml input file.'
    argument name: 'outputFile', description: 'The optional output file. If none is provided, then the output will go to System.out.', required: false
    argument name: 'indent', description: 'Optional indent. The default is 4 spaces', required: false
    argument name: 'escapeList', description: "An optional CSV list of values to escape, with no spaces. The default is 'default'", required: false
}

String ymlFile = args[0]
String outputFile = args[1]
String indent = args.size() > 2 ? ' ' * Integer.parseInt(args[2]) : ' ' * 4
List<String> quotedValues = args.size() > 3 ? args[3].split(',').toList() : ['default']

File config = new File(ymlFile)
String configText = config.newDataInputStream().getText()
List<String> docs = configText.split('---\n')
GroovyConfigWriter configWriter

if (outputFile) {
    configWriter = new GroovyConfigWriter(outputFile, null, indent, quotedValues)
} else {
    configWriter = new GroovyConfigWriter()
}
configWriter
Yaml yaml = new Yaml()

docs.findResults {
    configWriter.writeToGroovy(yaml.load(it))
}

configWriter.close()
