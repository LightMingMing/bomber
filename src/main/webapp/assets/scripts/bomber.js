function checkedIds() {
    let arr = []
    $("tr.selected td.checkbox input").each(function (index, item) {
        arr.push(item.value)
    })
    return arr.join(",")
}

function redirectTo(url) {
    window.open(url, '_blank')
}

$(function () {

    // generic header filed name
    const headerNames = ['Accept', 'Accept-Charset', 'Accept-Encoding', 'Accept-Language', 'Age', 'Allow', 'Authorization',
        'Cache-Control', 'Connection', 'Content-Encoding', 'Content-Language', 'Content-Length', 'Content-Type', 'Cookie',
        'Host', 'Location', 'Origin', 'Referer', 'Server', 'Set-Cookie'];

    const contentTypes = ['text/html', 'text/plain', 'application/xhtml+xml', 'application/json', 'application/x-www-form-urlencoded']

    $(document).on('focus.typeahead.data-api', '.header-name', function () {
        const $this = $(this);

        if ($this.data('typeahead')) {
            $this.data('typeahead').$element = $this
            $this.data('typeahead').source = headerNames
            return
        }
        $this.typeahead({
            source: headerNames,
            items: 5
        })
    })

    $(document).on('focus.typeahead.data-api', '.header-value', function () {
        const $this = $(this)
        const $name = $this.parent('td').prev('td').children('input')[0]

        if ($name !== undefined && $name.value === 'Content-Type') {
            if ($this.data('typeahead')) {
                $this.data('typeahead').$element = $this
                $this.data('typeahead').source = contentTypes
                return
            }
            $this.typeahead({
                source: contentTypes,
                items: 5
            })
        } else {
            if ($this.data('typeahead')) {
                $this.data('typeahead').$element = $this
                $this.data('typeahead').source = []
            }
        }
    })
});

$(function () {
    $(document).on('change', 'select.function-type', function () {
        const $this = $(this)
        const name = $(this).val()
        if (name === '') {
            return;
        }

        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: CONTEXT_PATH + "/api/functionMetadata/" + name,
            success: function (data) {
                const $tr = $this.parent('td').parent('tr')
                const requiredArgs = $tr.find('input.required-args')
                const optionalArgs = $tr.find('input.optional-args')

                if (requiredArgs.length !== 0 && data.requiredArgs !== undefined) {
                    requiredArgs[0].value = data.requiredArgs
                } else {
                    requiredArgs[0].value = ''
                }

                if (optionalArgs.length !== 0 && data.optionalArgs !== undefined) {
                    optionalArgs[0].value = data.optionalArgs
                } else {
                    optionalArgs[0].value = ''
                }
            }
        })
    })
});