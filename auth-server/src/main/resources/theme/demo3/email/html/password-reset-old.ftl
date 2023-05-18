<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
</head>

<body>
<table width="600" style="font-family: Arial, Helvetica, sans-serif; font-size: 0.875rem; color: rgb(51, 51, 51);">
    <tr>
        <td style="padding: 32px 48px 0 48px; display: flex; align-items:center;">
            <img src="http://103.69.124.84:9091/auth/resources/ftaqx/login/demo3/css/assets/images/nep-logo.svg" height="50px">
            <div style="font-weight: 600;font-size: 1rem; margin-left: 1.5rem;">एकीकृत कार्यालय व्यवस्थापन प्रणाली
            </div>
        </td>

    </tr>
    <tr>
        <td style="padding: 0 48px;">
            <div style="margin-bottom: 1.5rem;">${msg("passwordResetMessage1")}</div>
        </td>
    </tr>

    <tr>
        <td style="padding: 0 48px;">
            <div style="line-height: 1.375rem; margin-bottom: 1.5rem;">${msg("passwordResetMessage2")} <p> ${kcSanitize(msg("passwordResetHTMLLink",link))?no_esc} </p> </div>
        </td>
    </tr>
    <tr>
        <td style="padding: 0 48px;">
            <div style="line-height: 1.375rem;">${msg("passwordResetExpireText",linkExpirationFormatter(linkExpiration))} </div>
        </td>
    </tr>
    <tr>
        <td style="padding: 0 48px;">
            <div style="line-height: 1.375rem;  margin-bottom: 3rem">${msg("passwordResetOther")}</div>
        </td>
    </tr>

    <tr>
        <td style="background-color: rgb(33, 89, 171); padding: 1rem 0;">
            <table style="font-size: 0.75rem; color: rgb(255,255,255); width: 100%; text-align: center">
                <tr>
                    <td style="padding-bottom: 0.5rem">Copyright © 2021. All rights reserved</td>
                </tr>
                <tr>
                    <td>
                        <a href=" " style="color:#FFFFFF">GIOMS</a>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</body>

</html>
