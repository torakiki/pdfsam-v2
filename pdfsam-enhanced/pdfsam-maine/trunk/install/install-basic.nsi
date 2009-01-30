Name "pdfsam"

SetCompressor /SOLID zlib

# Defines
!define REGKEY "Software\$(^Name)"
!define VERSION 2.0.0-alpha
!define COMPANY "Andrea Vacondio"
!define URL "http://www.pdfsam.org/"

# MUI defines
!define MUI_ICON install-data\install.ico
!define MUI_FINISHPAGE_NOAUTOCLOSE
!define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKLM"
!define MUI_STARTMENUPAGE_REGISTRY_KEY ${REGKEY}
;!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME StartMenuGroup
!define MUI_STARTMENUPAGE_DEFAULTFOLDER "PDF Split And Merge"
!define MUI_WELCOMEFINISHPAGE_BITMAP install-data\install.bmp
!define MUI_UNICON install-data\uninstall.ico
!define MUI_UNFINISHPAGE_NOAUTOCLOSE
!define LANGUAGE_TITLE "pdfsam language selection"
!define MUI_LANGDLL_WINDOWTITLE "${LANGUAGE_TITLE}"

# Remember language for uninstallation
;!define MUI_LANGDLL_REGISTRY_ROOT HKLM
;!define MUI_LANGDLL_REGISTRY_KEY "Software\$(^Name)"
!define MUI_LANGDLL_REGISTRY_VALUENAME lang

# Included files
!include Sections.nsh
!include MUI.nsh
!include "WordFunc.nsh"
!include Library.nsh
!include "WinVer.nsh"
!include "nsDialogs.nsh"
!include "FileFunc.nsh"
#for modern UI functionality
!include "${NSISDIR}\Contrib\Modern UI\System.nsh"
!include "XML.nsh"

;Required functions
  !insertmacro GetParameters
  !insertmacro GetOptions
  !insertmacro un.GetParameters
  !insertmacro un.GetOptions
;--------------------------------

# Variables
  Var StartMenuGroup
  Var LANG_NAME
;user control
  Var ALL_USERS
  Var ALL_USERS_FIXED
  Var ALL_USERS_BUTTON
  Var IS_ADMIN
  Var USERNAME
; uninstaller variables
  Var un.REMOVE_ALL_USERS
  Var un.REMOVE_CURRENT_USER
;-------------------

# Installer pages
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE install-data\gpl.txt
Page custom PageAllUsers PageLeaveAllUsers ;call the user admin stuff
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_STARTMENU Application $StartMenuGroup
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH
!define MUI_PAGE_CUSTOMFUNCTION_PRE un.ConfirmPagePre ;for fancy uninstall
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!define MUI_PAGE_CUSTOMFUNCTION_PRE un.FinishPagePre ;for fancy uninstall
!insertmacro MUI_UNPAGE_FINISH

# Installer languages
  !insertmacro MUI_LANGUAGE "English" # first language is the default language
  !insertmacro MUI_LANGUAGE "Bosnian"
  !insertmacro MUI_LANGUAGE "Bulgarian"
  !insertmacro MUI_LANGUAGE "Croatian"
  !insertmacro MUI_LANGUAGE "Catalan"
  !insertmacro MUI_LANGUAGE "Czech"
  !insertmacro MUI_LANGUAGE "Danish"
  !insertmacro MUI_LANGUAGE "Galician"
  !insertmacro MUI_LANGUAGE "German"
  !insertmacro MUI_LANGUAGE "Greek"
  !insertmacro MUI_LANGUAGE "Spanish"
  !insertmacro MUI_LANGUAGE "Farsi"  
  !insertmacro MUI_LANGUAGE "Finnish"  
  !insertmacro MUI_LANGUAGE "French"
  !insertmacro MUI_LANGUAGE "Hebrew"
  !insertmacro MUI_LANGUAGE "Hungarian"
  !insertmacro MUI_LANGUAGE "Indonesian"
  !insertmacro MUI_LANGUAGE "Italian"
  !insertmacro MUI_LANGUAGE "Japanese"  
  !insertmacro MUI_LANGUAGE "Korean"
  !insertmacro MUI_LANGUAGE "Latvian"
  !insertmacro MUI_LANGUAGE "Lithuanian"
  !insertmacro MUI_LANGUAGE "Norwegian"
  !insertmacro MUI_LANGUAGE "Dutch"
  !insertmacro MUI_LANGUAGE "Polish"
  !insertmacro MUI_LANGUAGE "Portuguese"
  !insertmacro MUI_LANGUAGE "PortugueseBR"
  !insertmacro MUI_LANGUAGE "Russian"
  !insertmacro MUI_LANGUAGE "Slovak"
  !insertmacro MUI_LANGUAGE "Swedish"
  !insertmacro MUI_LANGUAGE "Thai"
  !insertmacro MUI_LANGUAGE "Turkish"
  !insertmacro MUI_LANGUAGE "Ukrainian"  
  !insertmacro MUI_LANGUAGE "SimpChinese"
  !insertmacro MUI_LANGUAGE "TradChinese"

# Installer attributes
OutFile pdfsam-win32inst-v2_0_0-alpha.exe
InstallDir "$PROGRAMFILES\pdfsam"
CRCCheck on
XPStyle on
ShowInstDetails show
VIProductVersion 2.0.0.0
RequestExecutionLevel highest
VIAddVersionKey /LANG=${LANG_ENGLISH} ProductName "pdfsam"
VIAddVersionKey /LANG=${LANG_ENGLISH} ProductVersion "${VERSION}"
VIAddVersionKey /LANG=${LANG_ENGLISH} CompanyName "${COMPANY}"
VIAddVersionKey /LANG=${LANG_ENGLISH} FileVersion "${VERSION}"
VIAddVersionKey /LANG=${LANG_ENGLISH} FileDescription ""
VIAddVersionKey /LANG=${LANG_ENGLISH} LegalCopyright "2009"
;InstallDirRegKey HKLM "${REGKEY}" Path
ShowUninstDetails hide

!define getLanguageName "!insertmacro getLanguageName"

#macro that resolves an id to an actual language
!macro getLanguageName code
    Push ${code}
    Call getLangName
!macroend

;function that gets called by the above macro
Function getLangName ;pretty sure there's a better way to do this...
    ClearErrors
    Pop $0
    ${Switch} $0
        ${Case} ${LANG_ENGLISH}
            Push 'en_GB' 
        ${Break}
        ${Case} ${LANG_ITALIAN}
            Push 'it' 
        ${Break}
        ${Case} ${LANG_BULGARIAN}
            Push 'bg' 
        ${Break}
        ${Case} ${LANG_BOSNIAN}
            Push 'bs' 
        ${Break}
        ${Case} ${LANG_CATALAN}
            Push 'ca' 
        ${Break}
        ${Case} ${LANG_CROATIAN}
            Push 'hr' 
        ${Break}
        ${Case} ${LANG_CZECH}
            Push 'cs' 
        ${Break}
        ${Case} ${LANG_SLOVAK}
            Push 'sk' 
        ${Break}
        ${Case} ${LANG_ITALIAN}
            Push 'it' 
        ${Break}
        ${Case} ${LANG_HEBREW}
            Push 'he' 
        ${Break}
        ${Case} ${LANG_RUSSIAN}
            Push 'ru' 
        ${Break}
        ${Case} ${LANG_NORWEGIAN}
            Push 'nb' 
        ${Break}  
        ${Case} ${LANG_SWEDISH}
            Push 'sv' 
        ${Break} 
        ${Case} ${LANG_SPANISH}
            Push 'es' 
        ${Break}
        ${Case} ${LANG_PORTUGUESE}
            Push 'pt_PT' 
        ${Break}
        ${Case} ${LANG_DUTCH}
            Push 'nl' 
        ${Break}   
        ${Case} ${LANG_FRENCH}
            Push 'fr' 
        ${Break}
        ${Case} ${LANG_GREEK}
            Push 'el' 
        ${Break}
        ${Case} ${LANG_TURKISH}
            Push 'tr' 
        ${Break}
        ${Case} ${LANG_GERMAN}
            Push 'de' 
        ${Break}
        ${Case} ${LANG_POLISH}
            Push 'pl' 
        ${Break}
        ${Case} ${LANG_FINNISH}
            Push 'fi' 
        ${Break}
        ${Case} ${LANG_SIMPCHINESE}
            Push 'zh_CN' 
        ${Break}
        ${Case} ${LANG_HUNGARIAN}
            Push 'hu' 
        ${Break}
        ${Case} ${LANG_DANISH}
            Push 'da' 
        ${Break}
        ${Case} ${LANG_TRADCHINESE}
            Push 'zh_TW' 
        ${Break}
        ${Case} ${LANG_INDONESIAN}
            Push 'id' 
        ${Break}
        ${Case} ${LANG_FARSI}
            Push 'fa' 
        ${Break}
        ${Case} ${LANG_KOREAN}
            Push 'ko' 
        ${Break}                
        ${Case} ${LANG_THAI}
            Push 'th' 
        ${Break}                
        ${Case} ${LANG_GALICIAN}
            Push 'gl' 
        ${Break}                
        ${Case} ${LANG_JAPANESE}
            Push 'ja' 
        ${Break}                
        ${Case} ${LANG_LATVIAN}
            Push 'lv' 
        ${Break}                
        ${Case} ${LANG_LITHUANIAN}
            Push 'lt' 
        ${Break}                
        ${Case} ${LANG_UKRAINIAN}
            Push 'uk' 
        ${Break}                
        ${Default}
            Push 'Default'
        ${Break}
    ${EndSwitch}
    Pop $LANG_NAME
FunctionEnd

Function WarnDirExists    
    IfFileExists $INSTDIR\*.* DirExists DirExistsOK
    DirExists:
    MessageBox MB_YESNO|MB_ICONQUESTION|MB_DEFBUTTON2 \
        "Installation directory already exists, please uninstall any previous installed version.$\nWould you like to install anyway?" \
        IDYES DirExistsOK
    Abort
    DirExistsOK:
FunctionEnd

# Installer sections
Section "-Install Section" SEC0000
    Call WarnDirExists
    SetOutPath $INSTDIR
    SetOverwrite on
    File /r F:\pdfsam\pdfsam-basic-2\*
    ;WriteRegStr HKLM "${REGKEY}\Components" "Install Section" 1
SectionEnd

Section -post SEC0001
    SetOutPath $INSTDIR
    WriteUninstaller $INSTDIR\uninstall.exe
    !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    CreateDirectory "$SMPROGRAMS\$StartMenuGroup"
    SetOutPath $INSTDIR
    ;use the INSTDIR outpath for the next shortcut in order to launch the JAR file properly
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\pdfsam.lnk" $INSTDIR\pdfsam-starter.exe
    SetOutPath $SMPROGRAMS\$StartMenuGroup
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\Readme.lnk" $INSTDIR\doc\readme.txt
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\Tutorial.lnk" $INSTDIR\doc\pdfsam-1.1.0-tutorial.pdf
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\Uninstall.lnk" $INSTDIR\uninstall.exe
    !insertmacro MUI_STARTMENU_WRITE_END
    
    WriteRegStr SHCTX "Software\$(^Name)" "" $INSTDIR
    WriteRegStr SHCTX "Software\$(^Name)" "Version" "${VERSION}"
    WriteRegExpandStr SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" "InstallLocation" "$INSTDIR"
    WriteRegExpandStr SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" UninstallString $INSTDIR\uninstall.exe
    WriteRegStr SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayName "$(^Name)"
    WriteRegStr SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayVersion "${VERSION}"
    WriteRegStr SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayIcon $INSTDIR\uninstall.exe
    WriteRegStr SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" "URLInfoAbout" "${URL}"
    WriteRegDWORD SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoModify 1
    WriteRegDWORD SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoRepair 1
  
SectionEnd

Section "-Write XML" ;writes to XML file
    
    ${xml::LoadFile} $INSTDIR\config.xml $0
    
    ${xml::GotoPath} "/pdfsam/settings/i18n" $0
    ${getLanguageName} $LANGUAGE
    
    ${xml::SetText} $LANG_NAME $1
    
    ${xml::SaveFile} "$INSTDIR\config.xml" $0
    
       
SectionEnd

# Uninstaller sections
Section "Uninstall"
  
  Delete "$INSTDIR\uninstall.exe"
  Delete "$INSTDIR\config.xml"
  Delete "$INSTDIR\pdfsam-starter.exe"
  Delete "$INSTDIR\*.jar"

  RMDir /r "$INSTDIR\lib"
  RMDir /r "$INSTDIR\bin"
  RMDir /r "$INSTDIR\plugins"
  RMDir /r "$INSTDIR\doc"
  RMDir "$INSTDIR"
   
    ${If} $un.REMOVE_ALL_USERS == 1
        SetShellVarContext all
    ${EndIf}
    ${If} $un.REMOVE_CURRENT_USER == 1
        SetShellVarContext current
    ${EndIf}
    Call un.RemoveStartmenu
    DeleteRegKey SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)"
    DeleteRegKey SHCTX "Software\$(^Name)"
SectionEnd

;This part controls installation for the current user / for all users
Function GetUserInfo

  ClearErrors
  UserInfo::GetName
  ${If} ${Errors}
    StrCpy $IS_ADMIN 1
    Return
  ${EndIf}

  Pop $USERNAME
  UserInfo::GetAccountType
  Pop $R0
  ${Switch} $R0
    ${Case} "Admin"
    ${Case} "Power"
      StrCpy $IS_ADMIN 1
      ${Break}
    ${Default}
      StrCpy $IS_ADMIN 0
      ${Break}
  ${EndSwitch}

FunctionEnd

Function ReadAllUsersCommandline

  ${GetParameters} $R0

  ${GetOptions} $R0 "/user" $R1

  ${Unless} ${Errors}
    ${If} $R1 == "current"
    ${OrIf} $R1 == "=current"
      ${If} $ALL_USERS_FIXED == 1
      ${AndIf} $ALL_USERS == 1
        MessageBox MB_ICONSTOP "Cannot install for current user only. pdfsam has been previously installed for all users.$\nPlease install this version for all users or uninstall previous version first."
        Abort
      ${EndIf}
      SetShellVarContext current
      StrCpy $ALL_USERS 0
      StrCpy $ALL_USERS_FIXED 1
    ${ElseIf} $R1 == "all"
    ${OrIf} $R1 == "=all"
      ${If} $ALL_USERS_FIXED == 1
      ${AndIf} $ALL_USERS == 0
        MessageBox MB_ICONSTOP "Cannot install for all users. pdfsam has been previously installed for the current user only.$\nPlease install this version for the current user only or uninstall previous version first."
        Abort
      ${EndIf}
      StrCpy $ALL_USERS 1
      StrCpy $ALL_USERS_FIXED 1
      SetShellVarContext all
    ${Else}
      MessageBox MB_ICONSTOP "Invalid option for /user. Has to be either /user=all or /user=current"
      Abort
    ${EndIf}
  ${EndUnless}
FunctionEnd

Function PageAllUsers

  !insertmacro MUI_HEADER_TEXT "Choose Installation Options" "Who should this application be installed for?"

  nsDialogs::Create /NOUNLOAD 1018
  Pop $0

  nsDialogs::CreateItem /NOUNLOAD STATIC ${WS_VISIBLE}|${WS_CHILD}|${WS_CLIPSIBLINGS} 0 0 0 100% 30 "Please select whether you wish to make this software available to all users or just yourself."
  Pop $R0
  
  ${If} $IS_ADMIN == 1
    ${If} $ALL_USERS_FIXED == 1
    ${AndIf} $ALL_USERS == 0
      StrCpy $R2 ${BS_AUTORADIOBUTTON}|${BS_VCENTER}|${BS_MULTILINE}|${WS_VISIBLE}|${WS_CHILD}|${WS_CLIPSIBLINGS}|${WS_GROUP}|${WS_TABSTOP}|${WS_DISABLED}
    ${Else}
      StrCpy $R2 ${BS_AUTORADIOBUTTON}|${BS_VCENTER}|${BS_MULTILINE}|${WS_VISIBLE}|${WS_CHILD}|${WS_CLIPSIBLINGS}|${WS_GROUP}|${WS_TABSTOP}
    ${EndIf}
  ${Else}
    StrCpy $R2 ${BS_AUTORADIOBUTTON}|${BS_VCENTER}|${BS_MULTILINE}|${WS_VISIBLE}|${WS_CHILD}|${WS_CLIPSIBLINGS}|${WS_GROUP}|${WS_TABSTOP}|${WS_DISABLED}
  ${EndIf}
  nsDialogs::CreateItem /NOUNLOAD BUTTON $R2 0 10 55 100% 30 "&Anyone who uses this computer (all users)"
  Pop $ALL_USERS_BUTTON
  
  ${If} $ALL_USERS_FIXED == 1
  ${AndIf} $ALL_USERS == 1
    StrCpy $R2 ${BS_AUTORADIOBUTTON}|${BS_TOP}|${BS_MULTILINE}|${WS_VISIBLE}|${WS_CHILD}|${WS_CLIPSIBLINGS}|${WS_DISABLED}
  ${Else}
    StrCpy $R2 ${BS_AUTORADIOBUTTON}|${BS_TOP}|${BS_MULTILINE}|${WS_VISIBLE}|${WS_CHILD}|${WS_CLIPSIBLINGS}
  ${EndIf}
  
  ${If} $USERNAME != ""
    nsDialogs::CreateItem /NOUNLOAD BUTTON $R2 0 10 85 100% 50 "&Only for me ($USERNAME)"
  ${Else}
    nsDialogs::CreateItem /NOUNLOAD BUTTON $R2 0 10 85 100% 50 "&Only for me"
  ${EndIf}
  Pop $R0
  
  ${If} $ALL_USERS_FIXED == 1
    ${If} $ALL_USERS == 1
      nsDialogs::CreateItem /NOUNLOAD STATIC ${WS_VISIBLE}|${WS_CHILD}|${WS_CLIPSIBLINGS} 0 0 -30 100% 30 "pdfsam has been previously installed for all users. Please uninstall first before changing setup type."
    ${Else}
      nsDialogs::CreateItem /NOUNLOAD STATIC ${WS_VISIBLE}|${WS_CHILD}|${WS_CLIPSIBLINGS} 0 0 -30 100% 30 "pdfsam has been previously installed for this user only. Please uninstall first before changing setup type."
    ${EndIf}
  ${Else}
    nsDialogs::CreateItem /NOUNLOAD STATIC ${WS_VISIBLE}|${WS_CHILD}|${WS_CLIPSIBLINGS} 0 0 -30 100% 30 "Installation for all users requires Administrator privileges."
  ${EndIf}
  Pop $R1

  ${If} $ALL_USERS == "1"
    SendMessage $ALL_USERS_BUTTON ${BM_SETCHECK} 1 0
  ${Else}
    SendMessage $R0 ${BM_SETCHECK} 1 0
  ${EndIf}
  
  nsDialogs::Show

FunctionEnd

Function PageLeaveAllUsers

  SendMessage $ALL_USERS_BUTTON ${BM_GETCHECK} 0 0 $R0
  ${If} $R0 == 0
    StrCpy $ALL_USERS "0"
    SetShellVarContext current
  ${Else}
    StrCpy $ALL_USERS "1"
    SetShellVarContext all
  ${EndIf}

FunctionEnd
;end user control-----------------------------------------------------------------

# Installer functions
Function .onInit
    
   ;prevent multiple instances of the installer
   System::Call 'kernel32::CreateMutexA(i 0, i 0, t "myNSISMutex") i .r1 ?e'
   Pop $R0
   StrCmp $R0 0 +3
   MessageBox MB_OK|MB_ICONEXCLAMATION "The installer is already running."
   Abort 
    
    ;Language selection dialog
    Push ""
    Push ${LANG_ENGLISH}
    Push English
    Push ${LANG_ITALIAN}
    Push Italian
    Push ${LANG_RUSSIAN}
    Push Russian
    Push ${LANG_BULGARIAN}
    Push Bulgarian
    Push ${LANG_SWEDISH}
    Push Swedish
    Push ${LANG_SPANISH}
    Push Spanish
    Push ${LANG_PORTUGUESE}
    Push Portuguese
    Push ${LANG_DUTCH}
    Push Dutch    
    Push ${LANG_FRENCH}
    Push French
    Push ${LANG_GERMAN}
    Push German
    Push ${LANG_GALICIAN}
    Push Galician
    Push ${LANG_GREEK}
    Push Greek
    Push ${LANG_NORWEGIAN}
    Push Norwegian
    Push ${LANG_TURKISH}
    Push Turkish
    Push ${LANG_POLISH}
    Push Polish
    Push ${LANG_FINNISH}
    Push Finnish
    Push ${LANG_SIMPCHINESE}
    Push SimpChinese
    Push ${LANG_HUNGARIAN}
    Push Hungarian
    Push ${LANG_DANISH}
    Push Danish
    Push ${LANG_FARSI}
    Push Farsi
    Push ${LANG_KOREAN}
    Push Korean
    Push ${LANG_JAPANESE}
    Push Japanese
    Push ${LANG_LATVIAN}
    Push Latvian
    Push ${LANG_LITHUANIAN}
    Push Lithuanian
    Push ${LANG_TRADCHINESE}
    Push TradChinese
    Push ${LANG_INDONESIAN}
    Push Indonesian
    Push ${LANG_BOSNIAN}
    Push Bosnian
    Push ${LANG_CZECH}
    Push Czech
    Push ${LANG_SLOVAK}
    Push Slovak
    Push ${LANG_THAI}
    Push Thai
    Push ${LANG_CATALAN}
    Push Catalan
    Push ${LANG_UKRAINIAN}
    Push Ukrainian
    Push ${LANG_CROATIAN}
    Push Croatian
    Push ${LANG_HEBREW}
    Push Hebrew  
    Push A ; A means auto count languages
           ; for the auto count to work the first empty push (Push "") must remain
    LangDLL::LangDialog "Installer Language" "Please select the language of the installer"
    
    Call GetUserInfo
    Call ReadAllUsersCommandline

    ${If} $ALL_USERS == 1
        ${If} $IS_ADMIN == 0    
            ${If} $ALL_USERS_FIXED == 1
                MessageBox MB_ICONSTOP "pdfsam has been previously installed for all users.$\nPlease restart the installer with Administrator privileges."
                Abort
            ${Else}
                StrCpy $All_USERS 0
            ${EndIf}
        ${EndIf}
    ${EndIf} 

    Pop $LANGUAGE
    StrCmp $LANGUAGE "cancel" 0 +2
        Abort
    
      
FunctionEnd

# Uninstaller functions
Function un.onInit

    ;copied 
    Call un.GetUserInfo
    Call un.ReadPreviousVersion
    
    ${If} $un.REMOVE_ALL_USERS == 1
    ${AndIf} $IS_ADMIN == 0
    MessageBox MB_ICONSTOP "$(^Name) has been installed for all users.$\nPlease restart the uninstaller with Administrator privileges to remove it."
    Abort
    ${EndIf}
    ;------------
    
    !insertmacro MUI_UNGETLANGUAGE
    ;TODO insert language dependent uninstall string below
    ;TODO uninstall prompt should not display location where files are being removed from
    MessageBox MB_ICONQUESTION|MB_YESNO|MB_DEFBUTTON2 "Are you sure you want to completely remove $(^Name)?" IDYES +2
    Abort
    
FunctionEnd

Function un.ReadPreviousVersion

  ReadRegStr $R0 HKLM "Software\$(^Name)" ""

  ${If} $R0 != ""
    ;Detect version
    ReadRegStr $R2 HKLM "Software\$(^Name)" "Version"
    ${If} $R2 == ""
      StrCpy $R0 ""
    ${EndIf}
  ${EndIf}

  ReadRegStr $R1 HKCU "Software\$(^Name)" ""
  
  ${If} $R1 != ""
    ;Detect version
    ReadRegStr $R2 HKCU "Software\$(^Name)" "Version"
    ${If} $R2 == ""
      StrCpy $R1 ""
    ${EndIf}
  ${EndIf}

  ${If} $R1 == $INSTDIR
    Strcpy $un.REMOVE_CURRENT_USER 1
  ${EndIf}
  ${If} $R0 == $INSTDIR
    Strcpy $un.REMOVE_ALL_USERS 1
  ${EndIf}
  ${If} $un.REMOVE_CURRENT_USER != 1
  ${AndIf} $un.REMOVE_ALL_USERS != 1
    ${If} $R1 != ""
      Strcpy $un.REMOVE_CURRENT_USER 1
      ${If} $R0 == $R1
        Strcpy $un.REMOVE_ALL_USERS 1
      ${EndIf}
    ${Else}
      StrCpy $un.REMOVE_ALL_USERS = 1
    ${EndIf}
  ${EndIf}

FunctionEnd

Function un.RemoveStartmenu
  !insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuGroup
  
  Delete "$SMPROGRAMS\$StartMenuGroup\Uninstall.lnk"
  Delete "$SMPROGRAMS\$StartMenuGroup\pdfsam.lnk"
  Delete "$SMPROGRAMS\$StartMenuGroup\readme.lnk"
  Delete "$SMPROGRAMS\$StartMenuGroup\tutorial.lnk"
  RMDir "$SMPROGRAMS\$StartMenuGroup"
FunctionEnd

;fancy uninstaller sections
Function un.onUninstSuccess
  HideWindow
  ;if we want a box saying "pdfsam completely removed we will uncomment below"
  ;MessageBox MB_ICONINFORMATION|MB_OK "$(^Name) completely removed."
FunctionEnd

Function un.GetUserInfo
  ClearErrors
  UserInfo::GetName
  ${If} ${Errors}
    StrCpy $IS_ADMIN 1
    Return
  ${EndIf}

  Pop $USERNAME
  UserInfo::GetAccountType
  Pop $R0
  ${Switch} $R0
    ${Case} "Admin"
    ${Case} "Power"
      StrCpy $IS_ADMIN 1
      ${Break}
    ${Default}
      StrCpy $IS_ADMIN 0
      ${Break}
  ${EndSwitch}

FunctionEnd

Function un.ConfirmPagePre
  
  ${un.GetParameters} $R0

  ${un.GetOptions} $R0 "/frominstall" $R1
  ${Unless} ${Errors}
    Abort
  ${EndUnless}

FunctionEnd

Function un.FinishPagePre
  
  ${un.GetParameters} $R0

  ${un.GetOptions} $R0 "/frominstall" $R1
  ${Unless} ${Errors}
    SetRebootFlag false
    Abort
  ${EndUnless}

FunctionEnd
;--------------------------

