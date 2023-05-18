<#import "template-lg.ftl" as layout>
<@layout.registrationLayout displayInfo=social.displayInfo; section>
   <#if section = "title">
      ${msg("doChangePassword")}
   <#elseif section = "header">
      <link rel="stylesheet" type="text/css"
            href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css"/>
      <script></script>
   <#elseif section = "form">
      <div class="login-form">
         <h5 class="heading-01">${msg("doChangePassword")}</h5>
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
         " action="${url.loginAction}" method="post" id="updatePassword">
         <div class="form-group">
            <label for="" class="d-block">
               <div class="align-vertical justify-content-between">
                  <span class="d-block">${msg("newPassword")}*</span>
                  <a href="${url.loginRestartFlowUrl}" class="text-primary small d-none d-md-block"
                  >${msg("doLogIn")}</a
                  >
               </div>
            </label>
            <div class="form-control-icon rft">
               <input id="hideUnhidePsw1" type="password" class="form-control" name="password-new" />
               <a id="hideUnhide1" href="#" class="ic-hidden"></a>
            </div>
         </div>
         <div class="form-group">
            <label for="">${msg("confirmPassword")}*</label>
            <div class="form-control-icon rft">
               <input id="hideUnhidePsw2" type="password" class="form-control" name="password-confirm" />
               <a id="hideUnhide2" href="#" class="ic-hidden"></a>
            </div>
         </div>

         <div class="row mb-1">
            <div class="col-lg-6">

               <ul class="login-list">
                  <li id="whiteSpaceId">पासवर्डमा खाली ठाउँ हुन हुँदैन ।</li>
                  <li id="capitalId">पासवर्डमा कम्तिमा एउटा ठूलो अक्षर हुन पर्दछ [A-Z] ।</li>
                  <li id="symbolId">पासवर्डमा कम्तिमा एउटा विशेष वर्ण हुन पर्दछ [@ $!% *? & ...] ।</li>
               </ul>
            </div>
            <div class="col-lg-6">
               <ul class="login-list">
                  <li id="sizeId">पासवर्ड ८ देखि ३० संख्याको हुन पर्दछ ।</li>
                  <li id="smallId">पासवर्डमा कम्तिमा एउटा सानो अक्षर हुन पर्दछ [a-z] ।</li>
                  <li id="numberId">पासवर्डमा कम्तिमा एक अंक हुन पर्दछ [0-9] ।</li>
               </ul>
            </div>
         </div>

         <button class="btn btn-primary btn-block login-button"  type="submit">
            ${msg('doSubmit')}
         </button>
         </form>

      </div>
   </#if>
</@layout.registrationLayout>
