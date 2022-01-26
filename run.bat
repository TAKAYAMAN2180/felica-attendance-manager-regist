set has_downloaded=false

if not exist "C:\\Program Files (x86)\\Sony\\FeliCa Secure Client" (
    echo "file not exit"
    ) else (
    echo "file exit"
    set has_downloaded=true
    )

echo %has_downloaded%