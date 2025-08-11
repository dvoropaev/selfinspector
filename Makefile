APK_PATH=app/build/outputs/apk/debug/app-debug.apk
ASSETS_DIR=app/src/main/assets/instructions

.PHONY: apk clean

apk: $(APK_PATH)
	@echo "APK built at $(APK_PATH)"

$(APK_PATH): $(wildcard instructions/*.wav) \
            $(shell find app -name '*.kt' -o -name '*.xml') \
            build.gradle settings.gradle app/build.gradle
	rm -rf app/src/main/assets
	mkdir -p $(ASSETS_DIR)
	cp instructions/*.wav $(ASSETS_DIR)/
	gradle assembleDebug

clean:
	rm -rf app/build .gradle app/src/main/assets
	gradle clean || true
