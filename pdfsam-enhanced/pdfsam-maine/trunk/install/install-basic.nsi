Name "pdfsam"

SetCompressor /SOLID lzma

# Defines
!define REGKEY "SOFTWARE\$(^Name)"
!define VERSION 1.0.0-b2
!define COMPANY "Andrea Vacondio"
!define URL ""

# MUI defines
!define MUI_ICON install-data\install.ico
!define MUI_FINISHPAGE_NOAUTOCLOSE
!define MUI_STARTMENUPAGE_REGISTRY_ROOT HKLM
!define MUI_STARTMENUPAGE_REGISTRY_KEY ${REGKEY}
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME StartMenuGroup
!define MUI_STARTMENUPAGE_DEFAULTFOLDER "PDF Split And Merge"
!define MUI_WELCOMEFINISHPAGE_BITMAP install-data\install.bmp
!define MUI_UNICON install-data\uninstall.ico
!define MUI_UNFINISHPAGE_NOAUTOCLOSE
!define LANGUAGE_TITLE "pdfsam language selection"
!define MUI_LANGDLL_WINDOWTITLE "${LANGUAGE_TITLE}"

# Remember language for uninstallation
!define MUI_LANGDLL_REGISTRY_ROOT HKLM
!define MUI_LANGDLL_REGISTRY_KEY "SOFTWARE\$(^Name)"
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

# Installer languages
  !insertmacro MUI_LANGUAGE "English" # first language is the default language
  !insertmacro MUI_LANGUAGE "Bosnian"
  !insertmacro MUI_LANGUAGE "Czech"
  !insertmacro MUI_LANGUAGE "Danish"
  !insertmacro MUI_LANGUAGE "German"
  !insertmacro MUI_LANGUAGE "Greek"
  !insertmacro MUI_LANGUAGE "Spanish"
  !insertmacro MUI_LANGUAGE "Finnish"  
  !insertmacro MUI_LANGUAGE "French"
  !insertmacro MUI_LANGUAGE "Hebrew"
  !insertmacro MUI_LANGUAGE "Hungarian"
  !insertmacro MUI_LANGUAGE "Indonesian"
  !insertmacro MUI_LANGUAGE "Italian"
  !insertmacro MUI_LANGUAGE "Dutch"
  !insertmacro MUI_LANGUAGE "Polish"
  !insertmacro MUI_LANGUAGE "Portuguese"
  !insertmacro MUI_LANGUAGE "PortugueseBR"
  !insertmacro MUI_LANGUAGE "Russian"
  !insertmacro MUI_LANGUAGE "Slovak"
  !insertmacro MUI_LANGUAGE "Swedish"
  !insertmacro MUI_LANGUAGE "Turkish"
  !insertmacro MUI_LANGUAGE "SimpChinese"
  !insertmacro MUI_LANGUAGE "TradChinese"

# Installer attributes
OutFile pdfsam-win32inst-v1_0_0-b2.exe
InstallDir "$PROGRAMFILES\pdfsam"
CRCCheck on
XPStyle on
ShowInstDetails show
VIProductVersion 1.0.0.0
RequestExecutionLevel highest
VIAddVersionKey /LANG=${LANG_ENGLISH} ProductName "pdfsam"
VIAddVersionKey /LANG=${LANG_ENGLISH} ProductVersion "${VERSION}"
VIAddVersionKey /LANG=${LANG_ENGLISH} CompanyName "${COMPANY}"
VIAddVersionKey /LANG=${LANG_ENGLISH} FileVersion "${VERSION}"
VIAddVersionKey /LANG=${LANG_ENGLISH} FileDescription ""
VIAddVersionKey /LANG=${LANG_ENGLISH} LegalCopyright ""
InstallDirRegKey HKLM "${REGKEY}" Path
ShowUninstDetails hide

# Macro for selecting uninstaller sections
!macro SELECT_UNSECTION SECTION_NAME UNSECTION_ID
    Push $R0
    ReadRegStr $R0 HKLM "${REGKEY}\Components" "${SECTION_NAME}"
    StrCmp $R0 1 0 next${UNSECTION_ID}
    !insertmacro SelectSection "${UNSECTION_ID}"
    GoTo done${UNSECTION_ID}
next${UNSECTION_ID}:
    !insertmacro UnselectSection "${UNSECTION_ID}"
done${UNSECTION_ID}:
    Pop $R0
!macroend

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
        	Push 'it_IT' 
        ${Break}
        ${Case} ${LANG_BOSNIAN}
            Push 'bs_BA' 
        ${Break}
        ${Case} ${LANG_CZECH}
            Push 'cs_CZ' 
        ${Break}
        ${Case} ${LANG_SLOVAK}
            Push 'sk_SK' 
        ${Break}
        ${Case} ${LANG_ITALIAN}
            Push 'it_IT' 
        ${Break}
        ${Case} ${LANG_HEBREW}
            Push 'he_IL' 
        ${Break}
        ${Case} ${LANG_RUSSIAN}
        	Push 'ru_RU' 
        ${Break} 
        ${Case} ${LANG_SWEDISH}
        	Push 'sv_SE' 
        ${Break} 
        ${Case} ${LANG_SPANISH}
        	Push 'es_ES' 
        ${Break}
        ${Case} ${LANG_PORTUGUESE}
        	Push 'pt_PT' 
        ${Break}
        ${Case} ${LANG_DUTCH}
        	Push 'nl_NL' 
        ${Break}   
        ${Case} ${LANG_FRENCH}
        	Push 'fr_FR' 
        ${Break}
        ${Case} ${LANG_GREEK}
        	Push 'el_GR' 
        ${Break}
        ${Case} ${LANG_TURKISH}
        	Push 'tr_TR' 
        ${Break}
        ${Case} ${LANG_GERMAN}
        	Push 'de_DE' 
        ${Break}
        ${Case} ${LANG_POLISH}
        	Push 'pl_PL' 
        ${Break}
        ${Case} ${LANG_FINNISH}
        	Push 'fi_FI' 
        ${Break}
        ${Case} ${LANG_SIMPCHINESE}
        	Push 'zh_CN' 
        ${Break}
        ${Case} ${LANG_HUNGARIAN}
        	Push 'hu_HU' 
        ${Break}
        ${Case} ${LANG_DANISH}
        	Push 'da_DK' 
        ${Break}
        ${Case} ${LANG_TRADCHINESE}
        	Push 'zh_TW' 
        ${Break}
        ${Case} ${LANG_INDONESIAN}
        	Push 'id_ID' 
        ${Break}
        ${Default}
        	Push 'en_NG'
        ${Break}
    ${EndSwitch}
    Pop $LANG_NAME
FunctionEnd


# Installer sections
Section "-Install Section" SEC0000
    SetOutPath $INSTDIR
    SetOverwrite on
    File /r F:\pdfsam\pdfsam-basic\*
    WriteRegStr HKLM "${REGKEY}\Components" "Install Section" 1
SectionEnd

Section -post SEC0001
    WriteRegStr HKLM "${REGKEY}" Path $INSTDIR
    SetOutPath $INSTDIR
    WriteUninstaller $INSTDIR\uninstall.exe
    !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    #SetOutPath $SMPROGRAMS\$StartMenuGroup
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\pdfsam.lnk" $INSTDIR\pdfsam-starter.exe
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\Readme.lnk" $INSTDIR\doc\readme.txt
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\Tutorial.lnk" $INSTDIR\doc\pdfsam-1.0.0-b2-tutorial.pdf
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\$(^UninstallLink).lnk" $INSTDIR\uninstall.exe
    !insertmacro MUI_STARTMENU_WRITE_END
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayName "$(^Name)"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayVersion "${VERSION}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" Publisher "${COMPANY}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayIcon $INSTDIR\uninstall.exe
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" UninstallString $INSTDIR\uninstall.exe
    WriteRegDWORD HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoModify 1
    WriteRegDWORD HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoRepair 1
SectionEnd

Section "-Write XML" ;writes to XML file
    
    ${xml::LoadFile} $INSTDIR\config.xml $0
    
    ${xml::GotoPath} "/pdfsam/settings/i18n" $0
    ${getLanguageName} $LANGUAGE
    
    ${xml::SetText} $LANG_NAME $1
    
    ${xml::SaveFile} "$INSTDIR\config.xml" $0
    
       
SectionEnd

# Uninstaller sections
Section /o "-un.Install Section" UNSEC0000
  Delete "$INSTDIR\uninst.exe"
  Delete "$INSTDIR\plugins\split\*.xml"
  Delete "$INSTDIR\plugins\split\*.jar"
  Delete "$INSTDIR\plugins\merge\*.xml"
  Delete "$INSTDIR\plugins\merge\*.jar"
  Delete "$INSTDIR\*.jar"
  Delete "$INSTDIR\*.exe"
  Delete "$INSTDIR\examples\*.*"
  Delete "$INSTDIR\licenses\*.exe"
  Delete "$INSTDIR\doc\*.txt"
  Delete "$INSTDIR\doc\*.pdf" 
  Delete "$INSTDIR\doc\licenses\bouncyCastle\*.*"
  Delete "$INSTDIR\doc\licenses\dom4j\*.*"
  Delete "$INSTDIR\doc\licenses\emp4j\*.*"
  Delete "$INSTDIR\doc\licenses\iText\*.*"
  Delete "$INSTDIR\doc\licenses\jaxen\*.*"
  Delete "$INSTDIR\doc\licenses\log4j\*.*"
  Delete "$INSTDIR\doc\licenses\looks\*.*"
  Delete "$INSTDIR\doc\licenses\pdfsam\*.*"
  Delete "$INSTDIR\doc\licenses\jcmdline\*.*" 
  Delete "$INSTDIR\lib\*.jar"
  Delete "$INSTDIR\config.xml"

  RMDir "$SMPROGRAMS\$StartMenuGroup"
  RMDir "$INSTDIR\lib"
  RMDir "$INSTDIR\plugins\split"
  RMDir "$INSTDIR\plugins\merge"
  RMDir "$INSTDIR\plugins"
  RMDir "$INSTDIR\doc\licenses\bouncyCastle"
  RMDir "$INSTDIR\doc\licenses\dom4j"
  RMDir "$INSTDIR\doc\licenses\emp4j"
  RMDir "$INSTDIR\doc\licenses\iText"
  RMDir "$INSTDIR\doc\licenses\jaxen"
  RMDir "$INSTDIR\doc\licenses\log4j"
  RMDir "$INSTDIR\doc\licenses\looks"
  RMDir "$INSTDIR\doc\licenses\pdfsam"
  RMDir "$INSTDIR\doc\licenses\jcmdline" 
  RMDir "$INSTDIR\doc"
  RMDir "$INSTDIR"
  RMDir ""
    
  DeleteRegValue HKLM "${REGKEY}\Components" "Install Section"
  
    ${If} $un.REMOVE_ALL_USERS == 1
        SetShellVarContext all
        Call un.RemoveStartmenu
    
        DeleteRegKey /ifempty HKLM "Software\pdfsam"
    ${EndIf}
    ${If} $un.REMOVE_CURRENT_USER == 1
        SetShellVarContext current
        Call un.RemoveStartmenu
    
        DeleteRegKey /ifempty HKCU "Software\pdfsam"
    ${EndIf}
SectionEnd

Section -un.post UNSEC0001
    DeleteRegKey HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)"
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\$(^UninstallLink).lnk"
    Delete /REBOOTOK $INSTDIR\uninstall.exe
    DeleteRegValue HKLM "${REGKEY}" StartMenuGroup
    DeleteRegValue HKLM "${REGKEY}" Path
    DeleteRegKey /IfEmpty HKLM "${REGKEY}\Components"
    DeleteRegKey /IfEmpty HKLM "${REGKEY}"
    RmDir /REBOOTOK $SMPROGRAMS\$StartMenuGroup
    RmDir /r $INSTDIR
    Push $R0
    StrCpy $R0 $StartMenuGroup 1
    StrCmp $R0 ">" no_smgroup
no_smgroup:
    Pop $R0
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
      nsDialogs::CreateItem /NOUNLOAD STATIC ${WS_VISIBLE}|${WS_CHILD}|${WS_CLIPSIBLINGS} 0 0 -30 100% 30 "FileZilla has been previously installed for all users. Please uninstall first before changing setup type."
    ${Else}
      nsDialogs::CreateItem /NOUNLOAD STATIC ${WS_VISIBLE}|${WS_CHILD}|${WS_CLIPSIBLINGS} 0 0 -30 100% 30 "FileZilla has been previously installed for this user only. Please uninstall first before changing setup type."
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
    Push ${LANG_GREEK}
    Push Greek
    Push ${LANG_TURKISH}
    Push Turkish
    Push ${LANG_GERMAN}
    Push German
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
    Push ${LANG_HEBREW}
    Push Hebrew     
    Push A ; A means auto count languages
           ; for the auto count to work the first empty push (Push "") must remain
    LangDLL::LangDialog "Installer Language" "Please select the language of the installer"

    Pop $LANGUAGE
    StrCmp $LANGUAGE "cancel" 0 +2
        Abort
    
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
FunctionEnd

# Uninstaller functions
Function un.onInit

    !insertmacro MUI_UNGETLANGUAGE
    MessageBox MB_ICONQUESTION|MB_YESNO|MB_DEFBUTTON2 "Are you sure you want to completely remove $(^Name)?" IDYES +2
    Abort
    Call un.GetUserInfo
    ${If} $un.REMOVE_ALL_USERS == 1
        ${AndIf} $IS_ADMIN == 0
        MessageBox MB_ICONSTOP "pdfsam has been installed for all users.$\nPlease restart the uninstaller with Administrator privileges to remove it."
        Abort
    ${EndIf}
    ReadRegStr $INSTDIR HKLM "${REGKEY}" Path
    !insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuGroup
    !insertmacro SELECT_UNSECTION "Install Section" ${UNSEC0000}
FunctionEnd

Function un.RemoveStartmenu
  Delete "$SMPROGRAMS\$StartMenuGroup\Uninstall.lnk"
  Delete "$SMPROGRAMS\$StartMenuGroup\pdfsam.lnk"
  Delete "$SMPROGRAMS\$StartMenuGroup\readme.lnk"
  Delete "$SMPROGRAMS\$StartMenuGroup\tutorial.lnk"
  Delete "$STARTMENU.lnk"
FunctionEnd

;fancy uninstaller sections
Function un.onUninstSuccess
  HideWindow
  MessageBox MB_ICONINFORMATION|MB_OK "$(^Name) completely removed."
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

# Installer Language Strings
# TODO Update the Language Strings with the appropriate translations.

LangString ^UninstallLink ${LANG_ENGLISH} "Uninstall $(^Name)"
LangString ^UninstallLink ${LANG_ITALIAN} "Uninstall $(^Name)"
LangString ^UninstallLink ${LANG_RUSSIAN} "Uninstall $(^Name)"
LangString ^UninstallLink ${LANG_SWEDISH} "Uninstall $(^Name)"
LangString ^UninstallLink ${LANG_SPANISH} "Uninstall $(^Name)"
LangString ^UninstallLink ${LANG_PORTUGUESE} "Uninstall $(^Name)"
LangString ^UninstallLink ${LANG_DUTCH} "Uninstall $(^Name)"
LangString ^UninstallLink ${LANG_FRENCH} "Désinstaller $(^Name)"
LangString ^UninstallLink ${LANG_GREEK} "Uninstall $(^Name)"
LangString ^UninstallLink ${LANG_TURKISH} "Uninstall $(^Name)"
LangString ^UninstallLink ${LANG_GERMAN} "Uninstall $(^Name)"
LangString ^UninstallLink ${LANG_POLISH} "Uninstall $(^Name)"
LangString ^UninstallLink ${LANG_FINNISH} "Uninstall $(^Name)"
LangString ^UninstallLink ${LANG_SIMPCHINESE} "Uninstall $(^Name)"
LangString ^UninstallLink ${LANG_HUNGARIAN} "Uninstall $(^Name)"
LangString ^UninstallLink ${LANG_DANISH} "Uninstall $(^Name)"
LangString ^UninstallLink ${LANG_TRADCHINESE} "Uninstall $(^Name)"
LangString ^UninstallLink ${LANG_INDONESIAN} "Uninstall $(^Name)"
LangString ^UninstallLink ${LANG_CZECH} "Uninstall $(^Name)"
LangString ^UninstallLink ${LANG_SLOVAK} "Uninstall $(^Name)"
LangString ^UninstallLink ${LANG_BOSNIAN} "Uninstall $(^Name)"
LangString ^UninstallLink ${LANG_HEBREW} "Uninstall $(^Name)"
