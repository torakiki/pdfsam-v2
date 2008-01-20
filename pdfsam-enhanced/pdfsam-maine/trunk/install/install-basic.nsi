;This script is based on the FileZilla 3 installation script
;Written by Tim Kosse <mailto:tim.kosse@filezilla-project.org>
;
 !define DATA_DIR "F:\pdfsam\workspace-enhanced\pdfsam-maine\install"
 !define FILE_DIR "F:\pdfsam\pdfsam-basic"
 !define PRODUCT_NAME "pdfsam"
 !define PRODUCT_VERSION "1.0.0-b2"
 !define PRODUCT_PUBLISHER "Andrea Vacondio"
 !define PRODUCT_UNINST_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"
 !define PRODUCT_UNINST_ROOT_KEY "HKLM"
 !define PRODUCT_STARTMENU_REGVAL "NSIS:StartMenuDir"
 !define TARGET_FILE "config.xml"
 !define PRODUCT_DATE "18/01/2008"
 !define LANGUAGE_TITLE "pdfsam language selection"

;--------------------------------
;General

  ;Name and file
  Name "${PRODUCT_NAME} ${PRODUCT_VERSION}"
  OutFile "pdfsam-win32inst-v1_0_0_b2.exe"

  SetCompressor /SOLID LZMA

  ;Default installation folder
  InstallDir "$PROGRAMFILES\pdfsam"
  
  ;Get installation folder from registry if available
  InstallDirRegKey HKLM "Software\pdfsam" ""

  RequestExecutionLevel highest
;--------------------------------
;Include Modern UI and functions

  !include "MUI.nsh"
  !include "WordFunc.nsh"
  !include Library.nsh
  !include "WinVer.nsh"
  !include "nsDialogs.nsh"
  !include "FileFunc.nsh"

;--------------------------------
;Required functions

  !insertmacro GetParameters
  !insertmacro GetOptions
  !insertmacro un.GetParameters
  !insertmacro un.GetOptions
;--------------------------------
;Variables

  Var ALL_USERS
  Var ALL_USERS_FIXED
  Var ALL_USERS_BUTTON
  Var IS_ADMIN
  Var USERNAME
  Var ICONS_GROUP
 ;--------------------------------
;Interface Settings

  !define MUI_ICON "${DATA_DIR}/install.ico"
  !define MUI_UNICON "${DATA_DIR}/uninstall.ico"
  !define MUI_ABORTWARNING 
  !define MUI_LANGDLL_WINDOWTITLE "${LANGUAGE_TITLE}"

  !insertmacro MUI_PAGE_LICENSE "${DATA_DIR}\gpl.txt"
  Page custom PageAllUsers PageLeaveAllUsers
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY

 ;Start Menu Folder Page Configuration
  !define MUI_STARTMENUPAGE_DEFAULTFOLDER "PDF Split And Merge"
  !define MUI_STARTMENUPAGE_REGISTRY_ROOT "${PRODUCT_UNINST_ROOT_KEY}"
  !define MUI_STARTMENUPAGE_REGISTRY_KEY "${PRODUCT_UNINST_KEY}"
  !define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "${PRODUCT_STARTMENU_REGVAL}"
  
  !insertmacro MUI_PAGE_STARTMENU Application $ICONS_GROUP

  !define MUI_PAGE_CUSTOMFUNCTION_LEAVE PostInstPage
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_PAGE_FINISH
  
  !define MUI_PAGE_CUSTOMFUNCTION_PRE un.ConfirmPagePre
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  !define MUI_PAGE_CUSTOMFUNCTION_PRE un.FinishPagePre
  !insertmacro MUI_UNPAGE_FINISH
  
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
  

Function .onInit

  !insertmacro MUI_LANGDLL_DISPLAY
  
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

; Language files
  !insertmacro MUI_LANGUAGE "English" # first language is the default language
  !insertmacro MUI_LANGUAGE "Italian"
  !insertmacro MUI_LANGUAGE "Russian"
  !insertmacro MUI_LANGUAGE "Swedish"
  !insertmacro MUI_LANGUAGE "Spanish"
  !insertmacro MUI_LANGUAGE "Portuguese"
  !insertmacro MUI_LANGUAGE "Dutch"
  !insertmacro MUI_LANGUAGE "French"
  !insertmacro MUI_LANGUAGE "Greek"
  !insertmacro MUI_LANGUAGE "Turkish"
  !insertmacro MUI_LANGUAGE "German"
  !insertmacro MUI_LANGUAGE "Polish"
  !insertmacro MUI_LANGUAGE "Finnish"  
  !insertmacro MUI_LANGUAGE "SimpChinese"
  !insertmacro MUI_LANGUAGE "Hungarian"
  !insertmacro MUI_LANGUAGE "Danish"
  !insertmacro MUI_LANGUAGE "TradChinese"
  !insertmacro MUI_LANGUAGE "Indonesian"
  

!macro ReplaceBetweenXMLTab This AndThis With In
Push "${This}"
Push "${AndThis}"
Push "${With}"
Push "${In}"
 Call ReplaceBetween
!macroend

Function WarnDirExists    
    IfFileExists $INSTDIR\*.* DirExists DirExistsOK
    DirExists:
    MessageBox MB_YESNO|MB_ICONQUESTION|MB_DEFBUTTON2 \
        "Installation directory already exists. Would you like to install anyway?" \
        IDYES DirExistsOK
    Abort
    DirExistsOK:
FunctionEnd

Function ReplaceBetween
 Exch $R0 ; file
 Exch
 Exch $R1 ; replace with
 Exch 2
 Exch $R2 ; before this (marker 2)
 Exch 2
 Exch 3
 Exch $R3 ; after this  (marker 1)
 Exch 3
 Push $R4 ; marker 1 len
 Push $R5 ; marker pos
 Push $R6 ; file handle
 Push $R7 ; temp file handle
 Push $R8 ; temp file name
 Push $R9 ; current line string
 Push $0 ; current chop
 Push $1 ; marker 1 + text
 Push $2 ; marker 2 + text
 Push $3 ; marker 2 len

 GetTempFileName $R8
 FileOpen $R7 $R8 w
 FileOpen $R6 $R0 r

 StrLen $3 $R3
 StrLen $R4 $R2

 Read1:
  ClearErrors
  FileRead $R6 $R9
  IfErrors Done
  StrCpy $R5 -1

 FindMarker1:
  IntOp $R5 $R5 + 1
  StrCpy $0 $R9 $3 $R5
  StrCmp $0 "" Write
  StrCmp $0 $R3 0 FindMarker1
   IntOp $R5 $R5 + $3
   StrCpy $1 $R9 $R5

  StrCpy $R9 $R9 "" $R5
  StrCpy $R5 0
  Goto FindMarker2

 Read2:
  ClearErrors
  FileRead $R6 $R9
  IfErrors Done
  StrCpy $R5 0

 FindMarker2:
  IntOp $R5 $R5 - 1
  StrCpy $0 $R9 $R4 $R5
  StrCmp $0 "" Read2
  StrCmp $0 $R2 0 FindMarker2
   StrCpy $2 $R9 "" $R5

   FileWrite $R7 $1$R1$2
   Goto Read1

 Write:
  FileWrite $R7 $R9
  Goto Read1

 Done:
  FileClose $R6
  FileClose $R7

  SetDetailsPrint none
  Delete $R0
  Rename $R8 $R0
  SetDetailsPrint both

 Pop $3
 Pop $2
 Pop $1
 Pop $0
 Pop $R9
 Pop $R8
 Pop $R7
 Pop $R6
 Pop $R5
 Pop $R4
 Pop $R3
 Pop $R2
 Pop $R1
 Pop $R0
FunctionEnd


Section "pdfsam" SEC01
;DetailPrint $LANGUAGE
;DetailPrint ${LANG_ENGLISH}
  Call WarnDirExists
  IfFileExists $INSTDIR\config.xml endFileExist
    SetOutPath "$INSTDIR"
    SetOverwrite try
    File "${FILE_DIR}\config.xml"
endFileExist:


;sostituisce la stringa tra i due tag
  !insertmacro ReplaceBetweenXMLTab "<version>" "</version>" "${PRODUCT_VERSION}" "$INSTDIR\config.xml"
  !insertmacro ReplaceBetweenXMLTab "<build_date>" "</build_date>" "${PRODUCT_DATE}" "$INSTDIR\config.xml"
;get language
    StrCmp $LANGUAGE 1033 +1 noenglish
      !insertmacro ReplaceBetweenXMLTab "<i18n>" "</i18n>" "en_GB" "$INSTDIR\config.xml"
    GOTO languagedone
    noenglish:
    StrCmp $LANGUAGE 1034 +1 nospanish
      !insertmacro ReplaceBetweenXMLTab "<i18n>" "</i18n>" "es_ES" "$INSTDIR\config.xml"
    GOTO languagedone
    nospanish:
    StrCmp $LANGUAGE 1036 +1 nofrench
      !insertmacro ReplaceBetweenXMLTab "<i18n>" "</i18n>" "fr_FR" "$INSTDIR\config.xml"
    GOTO languagedone
    nofrench:
    StrCmp $LANGUAGE 1049 +1 norussian
      !insertmacro ReplaceBetweenXMLTab "<i18n>" "</i18n>" "ru_RU" "$INSTDIR\config.xml"
    GOTO languagedone
    norussian:
    StrCmp $LANGUAGE 2070 +1 noportuguese
      !insertmacro ReplaceBetweenXMLTab "<i18n>" "</i18n>" "pt_BR" "$INSTDIR\config.xml"
    GOTO languagedone
    noportuguese:
    StrCmp $LANGUAGE 1040 +1 noitalian
      !insertmacro ReplaceBetweenXMLTab "<i18n>" "</i18n>" "it_IT" "$INSTDIR\config.xml"
    GOTO languagedone
    noitalian:
    StrCmp $LANGUAGE 1031 +1 nogerman
      !insertmacro ReplaceBetweenXMLTab "<i18n>" "</i18n>" "de_DE" "$INSTDIR\config.xml"
    GOTO languagedone
    nogerman:
    StrCmp $LANGUAGE 1053 +1 noswedish
      !insertmacro ReplaceBetweenXMLTab "<i18n>" "</i18n>" "sv_SE" "$INSTDIR\config.xml"
    GOTO languagedone
    noswedish:
    StrCmp $LANGUAGE 1043 +1 nodutch
      !insertmacro ReplaceBetweenXMLTab "<i18n>" "</i18n>" "nl_NL" "$INSTDIR\config.xml"
    GOTO languagedone
    nodutch:
    StrCmp $LANGUAGE 1032 +1 nogreek
      !insertmacro ReplaceBetweenXMLTab "<i18n>" "</i18n>" "el_EL" "$INSTDIR\config.xml"
    GOTO languagedone
    nogreek:
    StrCmp $LANGUAGE 1055 +1 noturkish
      !insertmacro ReplaceBetweenXMLTab "<i18n>" "</i18n>" "tr_TR" "$INSTDIR\config.xml"
    GOTO languagedone
     noturkish:
    StrCmp $LANGUAGE 1035 +1 nofinnish
      !insertmacro ReplaceBetweenXMLTab "<i18n>" "</i18n>" "fi_FI" "$INSTDIR\config.xml"
    GOTO languagedone
     nofinnish:
    StrCmp $LANGUAGE 1038 +1 nohungarian
      !insertmacro ReplaceBetweenXMLTab "<i18n>" "</i18n>" "hu_HU" "$INSTDIR\config.xml"
    GOTO languagedone
     nohungarian:
    StrCmp $LANGUAGE 1030 +1 nodanish
      !insertmacro ReplaceBetweenXMLTab "<i18n>" "</i18n>" "da_DA" "$INSTDIR\config.xml"
    GOTO languagedone
     nodanish:
    StrCmp $LANGUAGE 1045 +1 nopolish
      !insertmacro ReplaceBetweenXMLTab "<i18n>" "</i18n>" "pl_PL" "$INSTDIR\config.xml"
    GOTO languagedone
     nopolish:
    StrCmp $LANGUAGE 2052 +1 nosimpchinese
      !insertmacro ReplaceBetweenXMLTab "<i18n>" "</i18n>" "zh_CN" "$INSTDIR\config.xml"
    GOTO languagedone
     nosimpchinese:
    StrCmp $LANGUAGE 1028 +1 notradchinese
      !insertmacro ReplaceBetweenXMLTab "<i18n>" "</i18n>" "zh_TW" "$INSTDIR\config.xml"
    GOTO languagedone
     notradchinese:
    StrCmp $LANGUAGE 1057 +1 noindonesian
      !insertmacro ReplaceBetweenXMLTab "<i18n>" "</i18n>" "id_ID" "$INSTDIR\config.xml"
    GOTO languagedone
     noindonesian:
      !insertmacro ReplaceBetweenXMLTab "<i18n>" "</i18n>" "en_GB" "$INSTDIR\config.xml"
    QUIT
    languagedone:


  SetOverwrite on
  SetOutPath "$INSTDIR\lib"
  File "${FILE_DIR}\lib\*.jar"

  SetOutPath "$INSTDIR\bin"
  File "${FILE_DIR}\bin\*.*"
     
  SetOutPath "$INSTDIR"
  File "${FILE_DIR}\*.*"
  
  SetOutPath "$INSTDIR\doc"
  File "${FILE_DIR}\doc\*.txt"
  File "${FILE_DIR}\doc\*.pdf"
  
  SetOutPath "$INSTDIR\doc\examples"
  File "${FILE_DIR}\doc\examples\*.*"
  
  SetOutPath "$INSTDIR\doc\bouncyCastle"
  File "${FILE_DIR}\doc\licenses\bouncyCastle\*.*"
  
  SetOutPath "$INSTDIR\doc\dom4j"
  File "${FILE_DIR}\doc\licenses\dom4j\*.*"
  
  SetOutPath "$INSTDIR\doc\emp4j"
  File "${FILE_DIR}\doc\licenses\emp4j\*.*"
  
  SetOutPath "$INSTDIR\doc\iText"
  File "${FILE_DIR}\doc\licenses\iText\*.*"
  
  SetOutPath "$INSTDIR\doc\jaxen"
  File "${FILE_DIR}\doc\licenses\jaxen\*.*"
  
  SetOutPath "$INSTDIR\doc\log4j"
  File "${FILE_DIR}\doc\licenses\log4j\*.*"
  
  SetOutPath "$INSTDIR\doc\looks"
  File "${FILE_DIR}\doc\licenses\looks\*.*"
  
  SetOutPath "$INSTDIR\doc\pdfsam"
  File "${FILE_DIR}\doc\licenses\pdfsam\*.*"
  
  SetOutPath "$INSTDIR\doc\jcmdline"
  File "${FILE_DIR}\doc\licenses\jcmdline\*.*"
   
  SetOutPath "$INSTDIR\plugins\merge"
  File "${FILE_DIR}\plugins\merge\pdfsam-merge-0.6.1.jar"
  File "${FILE_DIR}\plugins\merge\config.xml"
  
  SetOutPath "$INSTDIR\plugins\split"
  File "${FILE_DIR}\plugins\split\pdfsam-split-0.4.1.jar"
  File "${FILE_DIR}\plugins\split\config.xml"
  
  SetOutPath "$SMPROGRAMS\$ICONS_GROUP"
SectionEnd

Section -AdditionalIcons
  SetOutPath $INSTDIR
  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
  CreateDirectory "$SMPROGRAMS\$ICONS_GROUP"
  CreateShortCut "$SMPROGRAMS\$ICONS_GROUP\pdfsam.lnk" "$INSTDIR\pdfsam-starter.exe"
  CreateShortCut "$SMPROGRAMS\$ICONS_GROUP\Readme.lnk" "$INSTDIR\doc\readme.txt"
  CreateShortCut "$SMPROGRAMS\$ICONS_GROUP\Tutorial.lnk" "$INSTDIR\doc\pdfsam-0.7b1-tutorial.pdf"
  CreateShortCut "$SMPROGRAMS\$ICONS_GROUP\Uninstall.lnk" "$INSTDIR\uninst.exe"
  !insertmacro MUI_STARTMENU_WRITE_END
SectionEnd

Section -Post
  WriteUninstaller "$INSTDIR\uninst.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayName" "$(^Name)"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "UninstallString" "$INSTDIR\uninst.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${PRODUCT_VERSION}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "Publisher" "${PRODUCT_PUBLISHER}"
SectionEnd


Function PostInstPage

    FindWindow $MUI_TEMP1 "#32770" "" $HWNDPARENT
    GetDlgItem $MUI_TEMP1 $MUI_TEMP1 1016
    System::Call user32::IsWindowVisible(i$MUI_TEMP1)i.s
    Pop $MUI_TEMP1

    StrCmp $MUI_TEMP1 0 +2
    SetAutoClose false

FunctionEnd

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

Function un.onInit
  
  MessageBox MB_ICONQUESTION|MB_YESNO|MB_DEFBUTTON2 "Are you sure you want to completely remove $(^Name)?" IDYES +2
  Abort
   
  Call un.GetUserInfo

  ${If} $un.REMOVE_ALL_USERS == 1
  ${AndIf} $IS_ADMIN == 0
    MessageBox MB_ICONSTOP "pdfsam has been installed for all users.$\nPlease restart the uninstaller with Administrator privileges to remove it."
    Abort
  ${EndIf}
  
FunctionEnd

Function un.RemoveStartmenu

  Delete "$SMPROGRAMS\$ICONS_GROUP\Uninstall.lnk"
  Delete "$SMPROGRAMS\$ICONS_GROUP\pdfsam.lnk"
  Delete "$SMPROGRAMS\$ICONS_GROUP\readme.lnk"
  Delete "$SMPROGRAMS\$ICONS_GROUP\tutorial.lnk"
  Delete "$STARTMENU.lnk"

FunctionEnd

Section Uninstall
  !insertmacro MUI_STARTMENU_GETFOLDER Application $ICONS_GROUP
  
  Delete "$INSTDIR\uninst.exe"
  Delete "$INSTDIR\plugins\split\*.xml"
  Delete "$INSTDIR\plugins\split\*.jar"
  Delete "$INSTDIR\plugins\merge\*.xml"
  Delete "$INSTDIR\plugins\merge\*.jar"
  Delete "$INSTDIR\*.jar"
  Delete "$INSTDIR\*.exe"
  Delete "$INSTDIR\examples\*.exe"
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

  RMDir "$SMPROGRAMS\$ICONS_GROUP"
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


  ${If} $un.REMOVE_ALL_USERS == 1
    SetShellVarContext all
    Call un.RemoveStartmenu

    DeleteRegKey /ifempty HKLM "Software\pdfsam"
    DeleteRegKey HKLM "${PRODUCT_UNINST_KEY}"

  ${EndIf}
  ${If} $un.REMOVE_CURRENT_USER == 1
    SetShellVarContext current
    Call un.RemoveStartmenu

    DeleteRegKey /ifempty HKCU "Software\pdfsam"
    DeleteRegKey HKCU "${PRODUCT_UNINST_KEY}"
  ${EndIf}

  SetAutoClose true
SectionEnd