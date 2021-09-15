# Before start
1. run init.sql script on database (if a user is needed)
2. move log file / files to a specific folder
3. fill in json file with log structure and put it into folder [tableJson](/src/main/resources/tableJSON)
4. fill the [config.properties](/src/main/resources/config.properties) file with the required data
    * a link to a database connection
    * username
    * password
    * path to json file with log structure
    * log file name or *, for all files
    * path to the folder with logs
    * archive flag (if there are archives in the folder(.rar))
    
# How the program works
The program loads the structure of the log table from json and creates a table based on it,
into which the logs will be loaded.

Json file should contain fields such as:
1. the name of the log table in which the parsed log entries will be stored
2. log parameter separator
3. column (field) name
4. the size of the VARCHAR field
5. left limiter of the log value (if any)
6. right limiter of the log value (if any)

You can configure the program in the config.properties file
1. db.url - link to the database connection
2. db.login - DB user login
3. db.password - DB user password
4. path.tablejson - json file with log structure
5. path.logfile - log file name
6. path.logfolder - path to the folder with logs

# Example of realization log file structure into json file
Log line: 
`0.0.0.0 - - [18/Apr/2021:15:59:52 +0000] "GET /Domen/ HTTP/1.0" 302 0 "-" "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89 Safari/537" 772 0.002 [namespace-8080] [] 01.01.0.1:8080 0 0.003 302 051retr128cwe9487f4d4548f1877737`

As you can see, the separator of the log parameters is a space. Next are the parameters:

1. parameter `10.40.0.0`, no left limiter (null), right one ` -` (space and dash, according to the structure of the nginx log)
2. parameter `-` (skipped), no left limiter (null) (null), as there is no right one
3. parameter `[18/Apr/2021:15:59:52 +0000]`, left limiter `[`, right one - `]`
4. parameter `"GET /NkaNet3/ HTTP/1.0"`, left limiter `"`, right one - `"` and etc.

Json file with such a set of parameters will look like this:

```json
{
   "tableName" : "nginx_controller",
   "separator" : " ",
   "tableNameForExceptRows" : "nginx_except",
   "columns": [
     {
       "columnName": "remote_addr",
       "columnSize": "100",
       "leftLimiter": "",
       "rightLimiter": " -"
     },
     {
       "columnName": "remote_user",
       "columnSize": "100",
       "leftLimiter": "",
       "rightLimiter": ""
     },
     {
       "columnName": "time_local",
       "columnSize": "50",
       "leftLimiter": "[",
       "rightLimiter": "]"
     },
     {
       "columnName": "request",
       "columnSize": "2000",
       "leftLimiter": "\"",
       "rightLimiter": "\""
     }
  ]
}

```


