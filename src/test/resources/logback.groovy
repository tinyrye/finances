appender('CONSOLE', ConsoleAppender) {
	encoder (PatternLayoutEncoder) {
		pattern = '%date{ISO8601} - %level - %logger: %msg%n'
	}
}
logger('com.softwhistle', ALL, ['CONSOLE'])
root(INFO, ['CONSOLE'])