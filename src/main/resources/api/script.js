$(function () {
    onHashChange();
    window.addEventListener('hashchange', onHashChange, false);

    $(window).scroll(function () {
        var fromTop = 50;

        var cur = $('.block').map(function () {
            if ($(this).offset().top - $(window).scrollTop() <= fromTop)
                return this;
        });

        cur = cur[cur.length - 1];
        var id = cur ? cur.id : '';

        $('.menu a').removeClass('active');
        $('[href="#' + id + '"]').addClass('active');
    });

    $(window).scroll();

    $('body').on('click', 'button', function () {
        $(this).closest('.response').hide().html('')
    });

    $('form').on('submit', function (event) {
        var thisForm = $(this);
        var responseEl = thisForm.children('.response');
        var url = $(event.target).attr('action');

        thisForm.find('input.replaceAction').each(function () {
            url = url.replace(new RegExp('{' + $(this).attr('name') + '}', 'g'), $(this).val());
            $(this).prop('disabled', true);
        });

        $.ajax({
            url: url,
            method: 'post',
            data: $(event.target).serializeArray(),
            contentType: "application/x-www-form-urlencoded"
        })
                .done(function (data, textStatus, jqXHR) {
                    responseEl.removeClass('fail');
                    responseEl.addClass('done');

                    processResponse(thisForm, responseEl, jqXHR);
                })
                .fail(function (jqXHR, textStatus, errorThrown) {
                    responseEl.removeClass('done');
                    responseEl.addClass('fail');

                    processResponse(thisForm, responseEl, jqXHR);
                });
        return false;
    });

    function onHashChange() {
        var hash = window.location.hash;
        $('.menu a').removeClass('active');
        $('[href="' + hash + '"]').addClass('active');
    }

    function processResponse(form, elementToChange, response) {
        var button = '<button>X</button>';
        var status =  '<p>' + response.status + ' ' + response.statusText + '</p>';
        var json = (response.responseJSON !== undefined)
                ? '<pre>' + JSON.stringify(response.responseJSON, null, 4) + '</pre>'
                : '';

        elementToChange.html(button + status + json);
        elementToChange.show();

        form.find('input.replaceAction').each(function () {
            $(this).prop('disabled', false);
        })
    }
});