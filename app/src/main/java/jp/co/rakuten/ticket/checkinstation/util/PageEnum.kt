package jp.co.rakuten.ticket.checkinstation.util

sealed class VisitPage(val pageName: String, val buttonName: String? = null)

//page action
//Menu
//home
object HomePage: VisitPage("Home")
//printer setting
object PrinterSettingPage: VisitPage("PrinterSetting")
//qrcode image setting
object QRCodeImageSettingPage: VisitPage("QRCodeImageSetting")
//qrcode read test
object QRCodeReadTestPage: VisitPage("QRCodeReadTest")
//ticket mode setting
object TicketModeSettingPage: VisitPage("TicketModeSetting")

//Login
//login
object LoginPage: VisitPage("Login")

//All
//all qrcode scan
object AllQRCodeScanPage: VisitPage("AllQRCodeScan")
//all input order number
object AllInputOrderNumberPage: VisitPage("AllInputOrderNumber")
//all input telephone
object AllInputTelephonePage: VisitPage("AllInputTelephone")
//all loading
object AllLoadingPage: VisitPage("AllLoading")
//all complete
object AllCompletePage: VisitPage("AllComplete")
//all input password
object AllInputPasswordPage: VisitPage("AllInputPassword")

//General
//general select mode
object GeneralSelectModePage: VisitPage("GeneralSelectMode")
//general qrcode scan
object GeneralQRCodeScanPage: VisitPage("GeneralQRCodeScan")
//general input order number
object GeneralInputOrderNumberPage: VisitPage("GeneralInputOrderNumber")
//general input telephone
object GeneralInputTelephonePage: VisitPage("GeneralInputTelephone")
//general select count mode
object GeneralSelectCountModePage: VisitPage("GeneralSelectCountMode")
//general need select
object GeneralNeedSelectPage: VisitPage("GeneralNeedSelect")
//general select targe
object GeneralSelectTargetPage: VisitPage("GeneralSelectTarget")
//general confirm
object GeneralConfirmPage: VisitPage("GeneralConfirm")
//general loading
object GeneralLoadingPage: VisitPage("GeneralLoading")
//general complete
object GeneralCompletePage: VisitPage("GeneralComplete")
//general input password
object GeneralInputPasswordPage: VisitPage("GeneralInputPassword")

//Single
//single qrcode scan
object SingleQRCodeScanPage: VisitPage("SingleQRCodeScan")
//single loading
object SingleLoadingPage: VisitPage("SingleLoading")
//single complete
object SingleCompletePage: VisitPage("SingleComplete")
//single input password
object SingleInputPasswordPage: VisitPage("SingleInputPassword")



//button action
//Menu
//print setting button
object HomePagePrinterSettingButton: VisitPage("Home", "PrinterSettingButton")
//print test button
object HomePagePrintTestButton: VisitPage("Home", "PrintTestButton")
//qrcode read test button
object HomePageQRCodeReadTestButton: VisitPage("Home", "QRCodeReadTestButton")
//ticket mode button
object HomePageTicketModeButton: VisitPage("Home", "TicketModeButton")
//qrcode image setting button
object HomePageQRCodeImageSettingButton: VisitPage("Home", "QRCodeImageSettingButton")
//login button
object HomePageLoginButton: VisitPage("Home", "LoginButton")
//print setting back button
object PrinterSettingBackButton: VisitPage("PrinterSetting", "BackButton")
//qrcode read test back button
object QRCodeReadTestBackButton: VisitPage("QRCodeReadTest", "BackButton")
//ticket mode back button
object TicketModeBackButton: VisitPage("TicketModeSetting", "BackButton")
//ticket mode all button
object TicketModeAllButton: VisitPage("TicketModeSetting", "AllButton")
//ticket mode general button
object TicketModeGeneralButton: VisitPage("TicketModeSetting", "GeneralButton")
//ticket mode single button
object TicketModeSingleButton: VisitPage("TicketModeSetting", "SingleButton")
//qrcode image setting back button
object QRCodeImageSettingBackButton: VisitPage("QRCodeImageSetting", "BackButton")
//qrcode image setting default image button
object QRCodeImageSettingDefaultImageButton: VisitPage("QRCodeImageSetting", "DefaultImageButton")
//qrcode image setting file image button
object QRCodeImageSettingFileImageButton: VisitPage("QRCodeImageSetting", "FileImageButton")

//Login
object LoginButton: VisitPage("Login", "LoginButton")
//login back button
object LoginBackButton: VisitPage("Login", "BackButton")

//All
//all qrcode scan order number button
object AllQRCodeScanOrderNumberButton: VisitPage("AllQRCodeScan", "OrderNumberButton")
//all qrcode scan menu button
object AllQRCodeScanMenuButton: VisitPage("AllQRCodeScan", "MenuButton")
//all qrcode logout button
object AllQRCodeScanLogoutButton: VisitPage("AllQRCodeScan", "LogoutButton")
//all qrcode scan reissue coupon button
object AllQRCodeScanReissueCouponButton: VisitPage("AllQRCodeScan", "ReissueCouponButton")
//all qrcode scan universal coupon button
object AllQRCodeScanUniversalCouponButton: VisitPage("AllQRCodeScan", "UniversalCouponButton")
//all input order number next button
object AllInputOrderNumberNextButton: VisitPage("AllInputOrderNumber", "NextButton")
//all input order number to qrcode scan button
object AllInputOrderNumberToQRCodeScanButton: VisitPage("AllInputOrderNumber", "ToQRCodeScanButton")
//all input telephone to qrcode scan button
object AllInputTelephoneToQRCodeScanButton: VisitPage("AllInputTelephone", "ToQRCodeScanButton")
//all input telephone to qrcode scan button
object AllInputTelephoneBackButton: VisitPage("AllInputTelephone", "BackButton")
//all input telephone to qrcode scan button
object AllInputTelephoneNextButton: VisitPage("AllInputTelephone", "NextButton")
//all input password menu button
object AllInputPasswordMenuButton: VisitPage("AllInputPassword", "MenuButton")
//all input password back button
object AllInputPasswordBackButton: VisitPage("AllInputPassword", "BackButton")
//all input password next button
object AllInputPasswordNextButton: VisitPage("AllInputPassword", "NextButton")

//General
//general select mode qrcode scan mode
object GeneralSelectModeQRCodeScanButton: VisitPage("GeneralSelectMode", "QRCodeScanButton")
//general select mode order number mode
object GeneralSelectOrderNumberButton: VisitPage("GeneralSelectMode", "OrderNumberButton")
//general select mode qrcode scan menu button
object GeneralSelectModeMenuButton: VisitPage("GeneralSelectMode", "MenuButton")
//general select mode qrcode logout button
object GeneralSelectModeLogoutButton: VisitPage("GeneralSelectMode", "LogoutButton")
//general select mode qrcode scan reissue coupon button
object GeneralSelectModeReissueCouponButton: VisitPage("GeneralSelectMode", "ReissueCouponButton")
//general select mode qrcode scan universal coupon button
object GeneralSelectModeUniversalCouponButton: VisitPage("GeneralSelectMode", "UniversalCouponButton")
//general qrcode scan menu button
object GeneralQRCodeScanMenuButton: VisitPage("GeneralQRCodeScan", "MenuButton")
//general qrcode logout button
object GeneralQRCodeScanLogoutButton: VisitPage("GeneralQRCodeScan", "LogoutButton")
//general qrcode scan reissue coupon button
object GeneralQRCodeScanReissueCouponButton: VisitPage("GeneralQRCodeScan", "ReissueCouponButton")
//general qrcode scan universal coupon button
object GeneralQRCodeScanUniversalCouponButton: VisitPage("GeneralQRCodeScan", "UniversalCouponButton")
//general qrcode scan to select mode button
object GeneralQRCodeScanToSelectModeButton: VisitPage("GeneralQRCodeScan", "ToSelectModeButton")
//general qrcode scan back button
object GeneralQRCodeScanBackButton: VisitPage("GeneralQRCodeScan", "BackButton")
//general input order number back button
object GeneralInputOrderNumberBackButton: VisitPage("GeneralInputOrderNumber", "BackButton")
//general input order number next button
object GeneralInputOrderNumberNextButton: VisitPage("GeneralInputOrderNumber", "NextButton")
//general input order number to qrcode scan button
object GeneralInputOrderNumberToQRCodeScanButton: VisitPage("GeneralInputOrderNumber", "ToSelectModeButton")
//general input telephone to qrcode scan button
object GeneralInputTelephoneToQRCodeScanButton: VisitPage("GeneralInputTelephone", "ToSelectModeButton")
//general input telephone back button
object GeneralInputTelephoneBackButton: VisitPage("GeneralInputTelephone", "BackButton")
//general input telephone next button
object GeneralInputTelephoneNextButton: VisitPage("GeneralInputTelephone", "NextButton")
//general select count mode to qrcode scan button
object GeneralSelectCountModeToQRCodeScanButton: VisitPage("GeneralSelectCountMode", "ToSelectModeButton")
//general select count mode back button
object GeneralSelectCountModeBackButton: VisitPage("GeneralSelectCountMode", "BackButton")
//general select count mode combined ticket
object GeneralSelectCountModeCombinedTicketButton: VisitPage("GeneralSelectCountMode", "CombinedTicketButton")
//general select count mode one ticket
object GeneralSelectCountModeOneTicketButton: VisitPage("GeneralSelectCountMode", "OneTicketButton")
//general need select to qrcode scan button
object GeneralNeedSelectToQRCodeScanButton: VisitPage("GeneralNeedSelect", "ToSelectModeButton")
//general need select back button
object GeneralNeedSelectBackButton: VisitPage("GeneralNeedSelect", "BackButton")
//general need select all ticket
object GeneralNeedSelectAllTicketButton: VisitPage("GeneralNeedSelect", "AllTicketButton")
//general need select ticket
object GeneralNeedSelectTicketButton: VisitPage("GeneralNeedSelect", "SelectTicketButton")
//general select targe to qrcode scan button
object GeneralSelectTargetToQRCodeScanButton: VisitPage("GeneralSelectTarget", "ToSelectModeButton")
//general select targe back button
object GeneralSelectTargetBackButton: VisitPage("GeneralSelectTarget", "BackButton")
//general select targe next button
object GeneralSelectTargetNextButton: VisitPage("GeneralSelectTarget", "NextButton")
//general confirm to qrcode scan button
object GeneralConfirmToQRCodeScanButton: VisitPage("GeneralConfirm", "ToSelectModeButton")
//general confirm back button
object GeneralConfirmBackButton: VisitPage("GeneralConfirm", "BackButton")
//general confirm next button
object GeneralConfirmNextButton: VisitPage("GeneralConfirm", "NextButton")
//general input password to qrcode scan button
object GeneralInputPasswordToQRCodeScanButton: VisitPage("GeneralInputPassword", "ToSelectModeButton")
//general input password back button
object GeneralInputPasswordBackButton: VisitPage("GeneralInputPassword", "BackButton")
//general input password next button
object GeneralInputPasswordNextButton: VisitPage("GeneralInputPassword", "NextButton")

//Single
//single qrcode scan menu button
object SingleQRCodeScanMenuButton: VisitPage("SingleQRCodeScan", "MenuButton")
//single qrcode logout button
object SingleQRCodeScanLogoutButton: VisitPage("SingleQRCodeScan", "LogoutButton")
//single qrcode scan reissue coupon button
object SingleQRCodeScanReissueCouponButton: VisitPage("SingleQRCodeScan", "ReissueCouponButton")
//single qrcode scan universal coupon button
object SingleQRCodeScanUniversalCouponButton: VisitPage("SingleQRCodeScan", "UniversalCouponButton")
//single input password menu button
object SingleInputPasswordMenuButton: VisitPage("SingleInputPassword", "MenuButton")
//single input password back button
object SingleInputPasswordBackButton: VisitPage("SingleInputPassword", "BackButton")
//single input password next button
object SingleInputPasswordNextButton: VisitPage("SingleInputPassword", "NextButton")