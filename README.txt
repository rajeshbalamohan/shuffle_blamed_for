Objective:
- Sometimes due to bad machines shuffle tries to blame the source nodes for bad data. If there are 100s of machine involved, it could take a little
bit of time to figure out which machine to look out for. This simple (too basic) tool helps in nailing down which machines to look out for.

1. mvn clean package.

2. java -cp ./target/*:./target/lib/*: ShuffleBlamedForParser yarn-app.log
This should genrate the output.txt in local folder, which should contain the details of task, source (which holds the data), fetcher machine (which is trying to pull data),
failure count etc. In console, the source and fetcher machine details are printed

3. E.g output in console. From this, it is easy to figure out that machine1088 needs to be concentrated on  (as all other nodes are pointing to this machine as the source)

src=attempt_1446684298977_3574_2_00_000706_0, srcMachine=machine1088, fetcher=attempt_1446684298977_3574_2_02_000177_0, fetcherMachine=d-dgd7j02, failure=72
src=attempt_1446684298977_3574_2_01_000193_0, srcMachine=machine1088, fetcher=attempt_1446684298977_3574_2_02_000174_0, fetcherMachine=d-dgd5j02, failure=73
src=attempt_1446684298977_3574_2_01_000478_0, srcMachine=machine1088, fetcher=attempt_1446684298977_3574_2_02_000133_0, fetcherMachine=d-2vzlw12, failure=91
src=attempt_1446684298977_3574_2_00_000402_0, srcMachine=machine1088, fetcher=attempt_1446684298977_3574_2_02_000062_0, fetcherMachine=d-dgf7j02, failure=113
src=attempt_1446684298977_3574_2_01_000003_0, srcMachine=machine1088, fetcher=attempt_1446684298977_3574_2_02_000011_0, fetcherMachine=d-dgf9j02, failure=133
src=attempt_1446684298977_3574_2_01_000288_0, srcMachine=machine1088, fetcher=attempt_1446684298977_3574_2_02_000180_0, fetcherMachine=d-2vwlw12, failure=71
src=attempt_1446684298977_3574_2_01_000269_0, srcMachine=machine1088, fetcher=attempt_1446684298977_3574_2_02_000027_0, fetcherMachine=d-d7rvfz1, failure=129
src=attempt_1446684298977_3574_2_01_000022_0, srcMachine=machine1088, fetcher=attempt_1446684298977_3574_2_02_000137_0, fetcherMachine=d-2vwlw12, failure=90


Source Machines being blamed for 
	machine1088


Fetcher Machines
	d-d7rvfz1
	d-dgf7j02
	machine1460
	d-2vwlw12
	d-2vzlw12
	d-d7zvfz1
	machine1459
	d-dgd5j02
	machine1080
	machine1082
	machine1164
	d-d7wsfz1
	d-dgd7j02
	d-dgf9j02
	machine1081
