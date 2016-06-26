/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Check from logs on whether any machine has to be checked for disk issues
 * or any other system related issues from logs.
 * <p>
 * Sometimes we get too many "blamed for read error" and need to check quickly
 * from logs on which machine to concentrate on.
 * <p>
 * If its 2.3.4, uncomment the pattern and recompile it.
 *
 * Usage: java -cp ./target/*:./target/lib/*: ShuffleBlamedForParser yarn.log
 *
 * This would generated output.txt which can be used for identifying bad nodes.
 */
public class ShuffleBlamedForParser {

  public static void main(String[] args) throws Exception {
    File inputFile = new File(args[0]);

    Preconditions.checkArgument(inputFile.exists(),
        "Please provide valid file; currentFile = " + inputFile);

    Pattern BLAMED_FOR = Pattern.compile("TaskAttemptImpl:(.*) blamed for read error from (.*) at");

    //HDP 2.3.4 (as the log file format changed)
    //BLAMED_FOR = Pattern.compile("TaskAttemptImpl\\|:(.*) blamed for read error from (.*) at");

    Pattern HOST_PATTERN = Pattern.compile("task=(.*), containerHost=(.*), localityMatchType");

    Map<String, String> hostMap = Maps.newHashMap();
    Map<String, Integer> fetcherFailure = Maps.newHashMap();

    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
      while (reader.ready()) {
        String line = reader.readLine();
        if (line.contains("task") && line.contains("containerHost")) {
          Matcher matcher = HOST_PATTERN.matcher(line);
          while (matcher.find()) {
            String attempt = matcher.group(1).trim();
            String host = matcher.group(2).trim();
            fetcherFailure.put(attempt, 0); //Just initializing
            hostMap.put(attempt, host);
          }
        }
      }
    }

    Set<String> hosts = new HashSet(hostMap.values());
    System.out.println("Unique hosts : " + hosts.size());

    Set<String> srcMachines = new HashSet<String>();
    Set<String> fetcherMachines = new HashSet<String>();

    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
      try (FileWriter writer = new FileWriter(new File(".", "output.txt"))) {
        while (reader.ready()) {
          String line = reader.readLine();
          if (line.contains("blamed for read error")) {
            Matcher matcher = BLAMED_FOR.matcher(line);
            while (matcher.find()) {
              String srcAttempt = matcher.group(1).trim();
              String fetcherAttempt = matcher.group(2).trim();
              fetcherFailure.put(fetcherAttempt, fetcherFailure.get(fetcherAttempt) + 1);
              if (hostMap.get(srcAttempt) == null) {
                System.out.println("ISSUE");
              }
              String s =
                    "src=" + srcAttempt
                  + ", srcMachine=" + hostMap.get(srcAttempt.trim())
                  + ", fetcher=" + fetcherAttempt
                  + ", fetcherMachine=" + hostMap.get(fetcherAttempt.trim())
                  //+ ", size=" + hostMap.size()
                  + ", failure=" + fetcherFailure.get(fetcherAttempt);
              srcMachines.add(hostMap.get(srcAttempt.trim()));
              fetcherMachines.add(hostMap.get(fetcherAttempt.trim()));
              System.out.println(s);
              writer.write(s + "\n");
            }
          }
        }
      }
    }

    //Summary
    System.out.println();
    System.out.println();
    System.out.println("Source Machines being blamed for ");
    for(String src : srcMachines) {
      System.out.println("\t" + src);
    }
    System.out.println();
    System.out.println();

    System.out.println("Fetcher Machines");
    for(String fetcher : fetcherMachines) {
      System.out.println("\t" + fetcher);
    }
  }
}
