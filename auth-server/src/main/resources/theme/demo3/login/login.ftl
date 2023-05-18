<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=social.displayInfo; section>
    <#if section = "title">
        <#if (realm.displayName!'')=='Keycloak'>
            ${msg("loginTitle",'Auth Server')}
        <#elseif (client.clientId!'')=='gerp-main'>
            ${msg("loginTitle",'GIOMS')}
        <#elseif (client.clientId!'')=='artifact-app'>
            ${msg("loginTitle",'Artifact')}
        </#if>
    <#elseif section = "header">
        <link rel="stylesheet" type="text/css"
              href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css"/>
        <script></script>
    <#elseif section = "form">
        <div class="login-form">
            <h5 class="heading-01">लग -इन</h5>
            <#if message?has_content>
                <#if message.type = 'success'>
                    <div class="alert alert-success mb-2" role="alert">
                        <i class="ic-checkmark mr-2"></i>
                        <p>
                            <span class="message-text">${message.summary?no_esc}</span>
                        </p>
                    </div>
                </#if>
                <#if message.type = 'warning'>
                    <div class="alert alert-warning mb-2" role="alert">
                        <i class="ic-alert mr-2"></i>
                        <p>
                            <span class="message-text">${message.summary?no_esc}</span>
                        </p>
                    </div>
                </#if>
                <#if message.type = 'error'>
                    <div class="alert alert-danger mb-2" role="alert">
                        <i class="ic-alert mr-2"></i>
                        <p class="text-danger">
                            <span class="message-text">${message.summary?no_esc}</span>
                        </p>
                    </div>
                </#if>
                <#if message.type = 'info'>
                    <div class="alert alert-primary mb-2" role="alert">
                        <i class="ic-info mr-2"></i>
                        <p>
                            <span class="message-text">${message.summary?no_esc}</span>
                        </p>
                    </div>
                </#if>
            </#if>

            <form return true;
            " action="${url.loginAction}" method="post">
                <div class="form-group">
                    <label for="">पीआईएस कोड</label>
                    <input type="text" class="form-control" name="username" />
                </div>
                <div class="form-group">
                    <label for="" class="">पासवर्ड</label>
                    <div class="form-control-icon rft">
                        <input id="hideUnhidePsw" type="password" class="form-control" name="password" />
                        <a id="hideUnhide" href="#" class="ic-hidden"></a>
                    </div>
<#--                    <a href="" class="text-primary small d-block d-md-none mt-2 text-right"-->
<#--                    >Forgot Password?</a-->
<#--                    >-->
                </div>
                <#if realm.resetPasswordAllowed>
                    <div class="align-vertical justify-content-end">
                        <a href="${url.loginResetCredentialsUrl}" class="text-primary small d-none d-md-block">${msg("doForgotPassword")}</a>
                    </div>
                </#if>

            <#if realm.rememberMe && !usernameEditDisabled??>
                <div class="custom-control custom-checkbox mr-sm-2">
                    <#if login.rememberMe??>
                        <input
                                name="rememberMe"
                                type="checkbox"
                                class="custom-control-input"
                                id="customControlAutosizing"
                        checked>
                        <label
                                class="custom-control-label"
                                for="customControlAutosizing"
                        >${msg("rememberMe")}</label
                        >
                    <#else>
                        <input
                                name="rememberMe"
                                type="checkbox"
                                class="custom-control-input"
                                id="customControlAutosizing"
                        >
                        <label
                                class="custom-control-label"
                                for="customControlAutosizing"
                        >${msg("rememberMe")}</label
                        >
                    </#if>

                </div>
            </#if>

                <button class="btn btn-primary btn-block login-button" value="${msg('doLogIn')}" type="submit">
                    लग -इन
                </button>
            </form>
        </div>
    </#if>
<#--        <#if social.providers??>-->
<#--            <div id="social-providers" style="text-align:center;">-->
<#--                <p><span style="text-align:center;"> or Login With </span></p>-->
<#--            </div>-->
<#--            <#list social.providers as p>-->
<#--                <div style="display: flex; margin-left:20px;">-->
<#--                <input class="${p.displayName}"-->
<#--                       style="margin-left:10;margin-right: 10"-->
<#--                       type="button" onclick="location.href='${p.loginUrl}';" value="${p.displayName}"/>-->
<#--            </#list>-->
<#--            </div>-->
<#--        </#if>-->
<#--        &lt;#&ndash;<div style="margin-top:50px;text-align:center">&ndash;&gt;-->
<#--        &lt;#&ndash;   <span style="text-size:16px;">Don't have an account?</span>&ndash;&gt;-->
<#--        &lt;#&ndash;   <a href="${url.registrationUrl}" style="color:#e6186d;">Signup</a>&ndash;&gt;-->
<#--        &lt;#&ndash;   <div>&ndash;&gt;-->
<#--        &lt;#&ndash;   </div>&ndash;&gt;-->
<#--        &lt;#&ndash;</div>&ndash;&gt;-->
<#--        </#if>-->
</@layout.registrationLayout>
