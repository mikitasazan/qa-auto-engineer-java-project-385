APP_IMAGE ?= hexletprojects/qa_auto_java_testing_kanban_board_project_ru_app
APP_CONTAINER ?= kanban-app
APP_PORT ?= 5173

.PHONY: install start stop restart test lint lint-fix

install:
	./gradlew testClasses

start:
	docker run --rm --name $(APP_CONTAINER) -p $(APP_PORT):5173 $(APP_IMAGE)

stop:
	- docker stop $(APP_CONTAINER)

restart: stop start

test:
	./gradlew test

lint:
	./gradlew spotlessCheck

lint-fix:
	./gradlew spotlessApply
