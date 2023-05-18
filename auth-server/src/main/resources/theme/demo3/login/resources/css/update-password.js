var testUpdatePassword = new Map();

var formRuleUpdatePassword = new Object();
var formMessageUpdatePassword = new Object();

const allUpdatePassword = {
    "password-new":[
        {
            name:'regex',
            rule:/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*\W)[A-Za-z\d\W]{8,30}$/,
            message:'पासवर्ड तल दिईएको अनुसार हुनु पर्छ ।'
        }
    ],
    "password-confirm":[
        {
            name:'equalTo',
            rule : "#hideUnhidePsw1",
            message:'यो फिल्ड पासवर्डसँग मिल्नु पर्छ ।'
        }
    ]
};

$("#updatePassword :input").each(function (e) {
    var rule = {};
    var message = {};
    if (testUpdatePassword.get($(this)[0].id) == null) {
        testUpdatePassword.set($(this)[0].id, 'set');
        if (!$(this).data('validfalse') && $(this)[0].name != "" && $(this)[0].type!="checkbox") {
            rule["required"] = true;
            message["required"] = "यो फिल्ड खाली हुन सक्दैन ।";
            const rules = allUpdatePassword[$(this)[0].name];
            if(rules)
                for(i = 0; i < rules.length ; i++){
                    rule[rules[i].name] = rules[i].rule;
                    message[rules[i].name] = rules[i].message;
                }
            formRuleUpdatePassword[$(this)[0].name] = rule;
            formMessageUpdatePassword[$(this)[0].name] = message;
        }
    }
});

var formUpdatePassword = $("#updatePassword");

formUpdatePassword.validate({
    ignore: [],
    rules: formRuleUpdatePassword,
    messages: formMessageUpdatePassword,

    onfocusout: function (element) {
        $(element).valid();
    },

    highlight: function (element, errorClass, validClass) {
        $(element.form).find('.actions').addClass('form-error');
        $(element).removeClass('valid');
        $(element).addClass('error');
    },
    unhighlight: function (element, errorClass, validClass) {
        $(element.form).find('.actions').removeClass('form-error');
        $(element).removeClass('error');
        $(element).addClass('valid');
    },
    errorPlacement: function (error, element) {
        if (element.parent().hasClass('form-control-icon')) {
            error.insertAfter(element.parent());
        } else {
            error.insertAfter(element);
        }


    }
});

$.validator.addMethod("regex", function (value, element, regexpr) {
    return regexpr.test(value);
}, "Please check your input.");

$('#updatePassword').submit(function(e){
    $(this).find(':input[type=submit]').prop('disabled', true);
    if(!$(this).valid()){
        $(this).find(':input[type=submit]').prop('disabled', false);
        return false;
    }

});

$('#hideUnhidePsw1').keyup(function(){
    // password size should be greater than 8 and less than 30
    checkStrength($('#hideUnhidePsw1').val(), '#sizeId',/.{8,30}/);

    // capital
    checkStrength($('#hideUnhidePsw1').val(), '#capitalId',/[A-Z]/);

    // small
    checkStrength($('#hideUnhidePsw1').val(), '#smallId',/[a-z]/);

    // symbol
    checkStrength($('#hideUnhidePsw1').val(), '#symbolId',/\W/);

    // number
    checkStrength($('#hideUnhidePsw1').val(), '#numberId',/[0-9]/);

    // whitespace
    checkStrengthFail($('#hideUnhidePsw1').val(), '#whiteSpaceId',/\s/);

})

function checkStrength(password, typeId, regex){
    // /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*\W)[A-Za-z\d\W]{8,30}$/

    // password size should be greater than 8 and less than 30
    if (password.match(regex)) {
        $(typeId).addClass('matched')
    }else{
        $(typeId).removeClass()
    }
}

function checkStrengthFail(password, typeId, regex){
    // /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*\W)[A-Za-z\d\W]{8,30}$/

    // password size should be greater than 8 and less than 30
    if (password.match(regex)) {
        $(typeId).removeClass()
    }else{
        $(typeId).addClass('matched')
    }
}
