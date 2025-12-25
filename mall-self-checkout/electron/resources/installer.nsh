; NSIS 安装脚本 - Mall自助收银系统
; 自定义安装配置

; 安装前检查
!macro customInit
  ; 检查是否已安装旧版本
  ReadRegStr $R0 HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${UNINSTALL_APP_KEY}" "UninstallString"
  StrCmp $R0 "" done

  ; 提示用户卸载旧版本
  MessageBox MB_OKCANCEL|MB_ICONEXCLAMATION \
    "检测到已安装旧版本的Mall自助收银系统。$\r$\n$\r$\n点击 '确定' 卸载旧版本并继续安装，或点击 '取消' 退出安装。" \
    IDOK uninst
  Abort

  uninst:
    ClearErrors
    ExecWait '$R0 _?=$INSTDIR'
    IfErrors no_remove_uninstaller done
    
  no_remove_uninstaller:
    Delete $R0
    RMDir $INSTDIR
    
  done:
!macroend

; 安装完成后的操作
!macro customInstall
  ; 创建防火墙规则
  ExecWait 'netsh advfirewall firewall add rule name="Mall自助收银系统" dir=in action=allow program="$INSTDIR\Mall自助收银系统.exe"'
  
  ; 注册文件关联（如果需要）
  WriteRegStr HKCR ".mall" "" "MallSelfCheckout"
  WriteRegStr HKCR "MallSelfCheckout" "" "Mall自助收银系统文件"
  WriteRegStr HKCR "MallSelfCheckout\DefaultIcon" "" "$INSTDIR\Mall自助收银系统.exe,0"
  WriteRegStr HKCR "MallSelfCheckout\shell\open\command" "" '"$INSTDIR\Mall自助收银系统.exe" "%1"'
!macroend

; 卸载时的操作
!macro customUnInit
  ; 删除防火墙规则
  ExecWait 'netsh advfirewall firewall delete rule name="Mall自助收银系统"'
  
  ; 删除文件关联
  DeleteRegKey HKCR ".mall"
  DeleteRegKey HKCR "MallSelfCheckout"
!macroend

; 安装页面自定义
!macro customWelcomePage
  !insertmacro MUI_PAGE_WELCOME
!macroend

; 许可协议页面（可选）
!macro customLicensePage
  ; 如果有许可协议文件，取消注释下面的行
  ; !insertmacro MUI_PAGE_LICENSE "license.txt"
!macroend 