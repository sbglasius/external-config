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

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

description("Converts a groovy config, to a yml config") {
    usage "groovy-to-yml-config [ymlFile] [optional outputFile] [optional indent] [optional style]"
    argument name: 'groovy', description: 'The groovy input file.'
    argument name: 'outputFile', description: 'The optional output file. If none is provided, then the output will go to System.out.', required: false
    argument name: 'indent', description: 'Sets the optional indent level for a file. The default is 4', required: false
    argument name: 'flow', description: 'Sets the optional style of BLOCK or FLOWS. The default is BLOCK', required: false
}

String groovyFile = args[0]
String outputFile = args[1]
int indent = args.size() > 2 ? Integer.parseInt(args[2]) : 4
DumperOptions.FlowStyle style = args.size() > 3 ? args[3] as DumperOptions.FlowStyle : DumperOptions.FlowStyle.BLOCK

File configFile = new File(groovyFile)
ConfigSlurper slurper = new ConfigSlurper()
def config = slurper.parse(configFile.toURL())


Writer configWriter

if (outputFile) {
    configWriter = new FileWriter(outputFile)
} else {
    configWriter = System.out.newPrintWriter()
}


final DumperOptions options = new DumperOptions()
options.setDefaultFlowStyle(style)
options.setPrettyFlow(true)
options.setIndent(indent)

Yaml yaml = new Yaml(options)

yaml.dump(config, configWriter)
