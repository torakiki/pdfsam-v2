@ECHO OFF

SET ProductVersion=2_2_2e
SET Culture=en-us
SET Arch=x86
REM SET Arch=x64
SET MsiName=pdfsam-%Arch%-v%ProductVersion%

REM Prevent compiling with outdated pdfsam.wixobj file if there is a error in candle.
del /Q pdfsam.wixobj 
del /Q RequirementsDlg.wixobj 
del /Q WixUI_PdfsamFeatureTree.wixobj 
del /Q VerifyWithLanguageDlg.wixobj

REM Build the MSI
"%WIX%bin\candle.exe" pdfsam.wxs RequirementsDlg.wxs WixUI_PdfsamFeatureTree.wxs VerifyWithLanguageDlg.wxs -ext WixUIExtension -ext WixUtilExtension -arch %Arch%

REM English
IF EXIST pdfsam.wixobj "%WIX%bin\light.exe" pdfsam.wixobj RequirementsDlg.wixobj VerifyWithLanguageDlg.wixobj WixUI_PdfsamFeatureTree.wixobj -ext WixUIExtension -ext WixUtilExtension -spdb -out "ReleaseDir\%MsiName%.msi" -loc "Lang\pdfsam.%Culture%.wxl" -cultures:%Culture%

REM German
REM SET Culture=de-de
REM IF EXIST pdfsam.wixobj "%WIX%bin\light.exe" pdfsam.wixobj -ext WixUIExtension -ext WixUtilExtension -spdb -out "ReleaseDir\%MsiName%.%Culture%.msi" -loc "Lang\pdfsam.%Culture%.wxl" -cultures:%Culture%

REM Create transform for German. Afterwards the German MSI can be deleted.
REM SET WinSDKVersion=v7.0
REM IF EXIST "%ProgramFiles%\Microsoft SDKs\Windows\%WinSDKVersion%\Bin\msitran.exe" "%ProgramFiles%\Microsoft SDKs\Windows\%WinSDKVersion%\Bin\msitran.exe" -g "ReleaseDir\pdfsam-2.2.1.en-us.msi" "ReleaseDir\pdfsam-2.2.1.de-de.msi" "ReleaseDir\1031.mst"

REM Cleanup
del /Q pdfsam.wixobj 
del /Q RequirementsDlg.wixobj 
del /Q WixUI_PdfsamFeatureTree.wixobj 
del /Q VerifyWithLanguageDlg.wixobj
SET Culture=
SET MsiName=
SET WinSDK=