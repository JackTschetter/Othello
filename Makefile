SRC := $(shell find src -name "*.java")
OUT := out

.PHONY: build cli gui test clean

build:
	mkdir -p $(OUT)
	javac -Xlint:all -d $(OUT) $(SRC)

cli: build
	java -cp $(OUT) cli.Main

gui: build
	java -cp $(OUT) ui.Main

test: build
	java -Xint -cp $(OUT) test.OthelloRulesTest

clean:
	rm -rf $(OUT)
