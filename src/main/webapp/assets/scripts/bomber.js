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
                const $tr = $this.closest('tr')
                const textarea = $tr.find('.argument-values')
                if (textarea.length !== 0) {
                    let content = []
                    if (data.requiredArgs !== undefined) {
                        content.push("# required args")
                        let args = data.requiredArgs.split(/,\s+/)
                        for (let i in args) {
                            content.push(args[i] + "=")
                        }
                    }
                    if (data.optionalArgs !== undefined) {
                        content.push("# optional args")
                        let args = data.optionalArgs.split(/,\s+/)
                        for (let i in args) {
                            content.push(args[i] + "=")
                        }
                    }
                    textarea[0].value = content.join("\n")
                }
            }
        })
    })
});