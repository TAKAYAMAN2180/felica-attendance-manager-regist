@echo off

rem 「C:\Program Files (x86)\Sony」の確認
if not exist "%PROGRAMFILES(X86)%\Sony" (
    echo "sony file not exit"
    mkdir "C:\Users\takay\Desktop\Sony"
    copy "Sony\*" "C:\Users\takay\Desktop"
) else (
    echo "sony file exit"

    rem 「C:\Program Files (x86)\Sony\FeliCa Secure Client」の確認
    if not exist "%PROGRAMFILES(X86)%\Sony\FeliCa Secure Client" (
        echo "FeliCa Secure Client file not exit"
        mkdir "%PROGRAMFILES(X86)%\Sony\FeliCa Secure Client"
        copy "Sony\FeliCa Secure Client\*" "C:\Users\takay\Desktop"
     ) else (
        echo "FeliCa Secure Client file exit"
     )

    rem 「C:\Program Files (x86)\Sony\FeliCaRW」の確認
    if not exist "%PROGRAMFILES(X86)%\Sony\FeliCaRW" (
        echo "FeliCaRW file not exit"
        mkdir "%PROGRAMFILES(X86)%\Sony\FeliCaRW"
        copy "Sony\FeliCaRW\*" "C:\Users\takay\Desktop"
    ) else (
        echo "FeliCaRW file exit"
    )

    rem 「C:\Program Files (x86)\Sony\NFC Proxy Service」の確認
    if not exist "%PROGRAMFILES(X86)%\Sony\NFC Proxy Service" (
       echo "NFC Proxy Service file not exit"
       mkdir "%PROGRAMFILES(X86)%\Sony\NFC Proxy Service"
       copy "Sony\NFC Proxy Service\*" "C:\Users\takay\Desktop"
    ) else (
       echo "NFC Proxy Service file exit"
    )
)

rem 「C:\Program Files (x86)\Common Files\Sony Shared」の確認
if not exist "%PROGRAMFILES(X86)%\Common Files\Sony Shared" (
    echo "Sony Shared file not exit"
    mkdir "C:\Users\takay\Desktop\Sony"
    copy "Sony Shared\*" "C:\Users\takay\Desktop"
) else (
    echo "Sony Shared file exit"

    rem 「C:\Program Files (x86)\Common Files\Sony Shared\FeliCaLibrary」の確認
    if not exist "%PROGRAMFILES(X86)%\Common Files\Sony Shared\FeliCaLibrary" (
        echo "FeliCaLibrary file not exit"
        mkdir "%PROGRAMFILES(X86)%\Common Files\Sony Shared\FeliCaLibrary"
        copy "Sony Shared\FeliCaLibrary\*" "C:\Users\takay\Desktop"
     ) else (
        echo "FeliCaLibrary file exit"
     )

    rem 「C:\Program Files (x86)\Sony\FeliCaNFCLibrary」の確認
    if not exist "%PROGRAMFILES(X86)%\Common Files\Sony Shared\FeliCaNFCLibrary" (
        echo "FeliCaNFCLibrary file not exit"
        mkdir "%PROGRAMFILES(X86)%\Common Files\Sony Shared\FeliCaNFCLibrary"
        copy "Sony Shared\FeliCaNFCLibrary\*" "C:\Users\takay\Desktop"
    ) else (
        echo "C:\Program Files (x86)\Sony\FeliCaNFCLibrary file exit" exist
    )
)

rem TODO:Pathが通っているかの確認
rem 「.\search.exe」は引数で渡されたパスが通っていれば「1」で通っていなければ「0」

rem パスを通す必要があるのは、「C:\Program Files (x86)\Common Files\Sony Shared\FeliCaLibrary」と「C:\Program Files (x86)\Common Files\Sony Shared\FeliCaNFCLibrary」
for /f "usebackq delims=" %%A in (`.\search.exe "%PROGRAMFILES(X86)%\Common Files\Sony Shared\FeliCaLibrary"`) do set has_FeliCaLibrary_path=%%A
if "%has_FeliCaLibrary_path%"=="0" (
    echo "「C:\Program Files (x86)\Common Files\Sony Shared\FeliCaLibrary」にパスが通ってません。"
    setx MYVAR "FeliCaLibrary foo bar"
) else (
    echo "「C:\Program Files (x86)\Common Files\Sony Shared\FeliCaLibrary」にパスが通ってます。"
)

for /f "usebackq delims=" %%A in (`.\search.exe "%PROGRAMFILES(X86)%\Common Files\Sony Shared\FeliCaNFCLibrary"`) do set has_FeliCaNFCLibrary_path=%%A
if "%has_FeliCaNFCLibrary_path%"=="0" (
    echo "「C:\Program Files (x86)\Common Files\Sony Shared\FeliCaNFCLibrary」にパスが通ってません。"
    setx MYVAR "FeliCaNFCLibrary foo bar"
) else (
    echo "「C:\Program Files (x86)\Common Files\Sony Shared\FeliCaNFCLibrary」にパスが通ってます。"
)

.\gradlew.bat run
