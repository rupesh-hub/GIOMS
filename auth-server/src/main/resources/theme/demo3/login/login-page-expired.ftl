<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "header">
        ${msg("pageExpiredTitle")}
        <link rel="stylesheet" type="text/css"
              href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css"/>
        <script></script>
    <#elseif section = "form">
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
        <div class="login-form">
            <p id="instruction1" class="instruction">
                ${msg("pageExpiredMsg1")} <a id="loginRestartLink" href="${url.loginRestartFlowUrl}">${msg("doClickHere")}</a> .<br/>
                ${msg("pageExpiredMsg2")} <a id="loginContinueLink" href="${url.loginAction}">${msg("doClickHere")}</a> .
            </p>

        </div>
    </#if>
</@layout.registrationLayout>
