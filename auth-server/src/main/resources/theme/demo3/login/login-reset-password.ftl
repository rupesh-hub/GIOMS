<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=social.displayInfo; section>
    <#if section = "title">
        ${msg("doForgotPassword")}
    <#elseif section = "header">
        <link rel="stylesheet" type="text/css"
              href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css"/>
        <script></script>
    <#elseif section = "form">
        <div class="login-form">
            <h5 class="heading-01">${msg("doForgotPassword")}</h5>
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
                <label for="" class="d-block">
                    <div class="align-vertical justify-content-between">
                        <span class="d-block">पीआईएस कोड</span>
                        <a href="${url.loginUrl}" class="text-primary small d-none d-md-block"
                        >${msg("doLogIn")}</a
                        >
                    </div>
                </label>
                <input type="text" class="form-control" name="username" />
            </div>

            <button class="btn btn-primary btn-block login-button"  type="submit">
                ${msg('doSubmit')}
            </button>
            </form>

        </div>
    </#if>
</@layout.registrationLayout>
