appender('FILE', RollingFileAppender) {
	file = '/var/log/softwhistle/finances-webservice.log'
	append = true
	encoder (PatternLayoutEncoder) {
		pattern = '%date{ISO8601} - %level - %logger: %msg%n'
	}
	rollingPolicy (FixedWindowRollingPolicy) {
		maxIndex = 5
		fileNamePattern = '/var/log/softwhistle/finances-webservice.log-%i.gz'
	}
	triggeringPolicy(SizeBasedTriggeringPolicy) {
        maxFileSize = '512MB'
    }
}
logger('com.softwhistle', ALL, ['FILE'])
root(INFO, ['FILE'])