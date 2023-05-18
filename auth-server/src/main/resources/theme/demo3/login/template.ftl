<#macro registrationLayout bodyClass="" displayInfo=false displayMessage=true>
    <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
            "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    <html xmlns="http://www.w3.org/1999/xhtml">

    <head>
        <meta charset="UTF-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <meta http-equiv="X-UA-Compatible" content="ie=edge"/>
        <link rel="shortcut icon" href="${url.resourcesPath}/img/favicon.ico" type="image/x-icon">
        <link
                href="https://fonts.googleapis.com/css?family=Nunito|Nunito+Sans&display=swap"
                rel="stylesheet"
        />
        <link href="style.css" rel="stylesheet"></head>

        <title><#nested "title"></title>
        <#if properties.styles?has_content>
            <#list properties.styles?split(' ') as style>
                <link href="${url.resourcesPath}/${style}" rel="stylesheet"/>
            </#list>
        </#if>
    </head>

    <body>
    <#--  <#nested "header">  -->

    <div class="login-container">
        <div class="row no-gutters align-items-center justify-content-center" style="width:800px;" >
            <div class="col-sm-8 col-md-6 col-lg-6">
                <div class="login-greeting">
                    <div class="align-vertical justify-content-center flex-column">
                        <div class="logo">
                            <img src="${url.resourcesPath}/css/assets/images/nep-logo.svg" alt="" />
                        </div>
                        <p class="text-white font-weight-bold">
                            नेपाल सरकार
                        </p>
<#--                        <select class="form-control language">-->
<#--                            <option value="english">EN</option>-->
<#--                            <option value="nepali">NP</option>-->
<#--                        </select>-->
                    </div>
                    <div class="login-divider mb-5"></div>
                    <div class="greeting text-center">
                        <h5>
                             <#if (realm.displayName!'')=='Keycloak'>
                                ${msg("loginTitle",'Auth Server')}
                             <#elseif (client.clientId!'')=='gerp-main'>
                                 एकीकृत कार्यालय व्यवस्थापन प्रणाली
                             <#elseif (client.clientId!'')=='artifact-app'>
<#--                                ${msg("welcomeTitle",'Artifact')} -->
                                 Artifact Repository and Knowledge Management System
                             </#if>
                        </h5>
                        <p class="mt-3">
                            <#if (realm.displayName!'')=='Keycloak'>
                                Configure Authentication Server
                            <#elseif (client.clientId!'')=='gerp-main'>
                                मा तपाईंलाई स्वागत छ
                            <#elseif (client.clientId!'')=='artifact-app'>
                                मा तपाईंलाई स्वागत छ
                            </#if>
                        </p>
                    </div>

                    <div class="login-footer">
                        <p>
                            <#if (realm.displayName!'')=='Keycloak'>
                                © 2021 Auth Server.
                            <#elseif (client.clientId!'')=='gerp-main'>
                                © <a href="https://doit.gov.np/np" target="_blank" rel="noopener noreferrer">सूचना प्रविधि विभाग</a>
                            <#elseif (client.clientId!'')=='artifact-app'>
                                © <a href="https://doit.gov.np/np" target="_blank" rel="noopener noreferrer">सूचना प्रविधि विभाग</a>
                            </#if>
                        </p>
<#--                        <div class="align-vertical">-->
<#--                            <p class="mr-3">Terms of Use</p>-->
<#--                            <p>Privacy Policy</p>-->
<#--                        </div>-->
                    </div>
                </div>
            </div>
            <div class="col-sm-8 col-md-5 col-lg-6">
                <#nested "form">
            </div>
        </div>
    </div>
<#--    <div class="page-wrapper bg-gra-01 p-t-100">-->
<#--        <div class="wrapper wrapper--w780">-->
<#--            <#if displayMessage && message?has_content>-->
<#--                <div class="alert alert-${message.type}">-->
<#--                    <#if message.type = 'success'><span class="${properties.kcFeedbackSuccessIcon!}"></span></#if>-->
<#--                    <#if message.type = 'warning'><span class="${properties.kcFeedbackWarningIcon!}"></span></#if>-->
<#--                    <#if message.type = 'error'><span class="${properties.kcFeedbackErrorIcon!}"></span></#if>-->
<#--                    <#if message.type = 'info'><span class="${properties.kcFeedbackInfoIcon!}"></span></#if>-->
<#--                    <span class="message-text">${message.summary?no_esc}</span>-->
<#--                </div>-->
<#--            </#if>-->
<#--        </div>-->
<#--    </div>-->

    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.4.1/jquery.min.js" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/jquery-validation@1.19.3/dist/jquery.validate.min.js" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/jquery-validation@1.19.3/dist/additional-methods.min.js" crossorigin="anonymous"></script>
    <script type="text/javascript" src="${url.resourcesPath}/css/update-password.js"></script>
    <script type="text/javascript" src="${url.resourcesPath}/css/assets/vendors.js"></script>
    <script type="text/javascript" src="${url.resourcesPath}/css/assets/home.js"></script>

    <script type="text/javascript">
        $('#hideUnhide').on('click',function () {
            if($(this).attr('class')=='ic-hidden'){
                $('#hideUnhidePsw').get(0).type = 'text';
                $(this).toggleClass('ic-hidden');
                $(this).toggleClass('ic-visible');
            }else{
                $('#hideUnhidePsw').get(0).type = 'password';
                $(this).toggleClass('ic-visible');
                $(this).toggleClass('ic-hidden');
            }
        })
    </script>

    </body>
    </html>
</#macro>
