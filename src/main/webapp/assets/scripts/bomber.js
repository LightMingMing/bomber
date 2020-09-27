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
                    let required = data.requiredArgs
                    if (required !== undefined && required.length > 0) {
                        content.push("# required args")
                        let args = data.requiredArgs.split(/,\s*/)
                        for (let i in args) {
                            content.push(args[i] + "=")
                        }
                    }
                    let optional = data.optionalArgs
                    if (optional !== undefined && optional.length > 0) {
                        content.push("# optional args")
                        let args = data.optionalArgs.split(/,\s*/)
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

$(function () {
    $(document).on('focus', 'input.payload-index, #requestsPerThread', function () {
        $(this).select()
    })

    $(document).on('change', 'input.payload-index', function () {
        const $this = $(this)
        const payloadIndex = $this.val()
        if (payloadIndex === '' || payloadIndex < 0)
            return;
        const uid = $("input.uid").val()
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: CONTEXT_PATH + "/httpSample/previewRequest?id=" + uid + "&payloadIndex=" + payloadIndex,
            success: function (data) {
                $("code.request").html(data.content)
            }
        })
    })

    $(document).on('click', 'button.execute', function () {
        const payloadIndex = $('input.payload-index').val()
        const uid = $('input.uid').val()
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: CONTEXT_PATH + "/httpSample/executeRequest?id=" + uid + "&payloadIndex=" + payloadIndex,
            success: function (data) {
                if (data.content !== undefined) {
                    $("#error").addClass("hidden")
                    $("#response").removeClass("hidden")
                    $("code.response").html(data.content)
                    $("span.elapsedTimeInMillis").html(data.elapsedTimeInMillis)
                } else {
                    $("#response").addClass("hidden")
                    $("#error").removeClass("hidden")
                    $("code.error").html(data.error)
                }
            }
        })
    })

    $(document).on('change', '#threadGroups, #requestsPerThread, #scope', function () {
        threadGroups = $("#threadGroups").val().split(/,\s*/)
        requestsPerThread = $("#requestsPerThread").val()
        scope = $("#scope").val()

        let totalThreads = 0;
        for (let i = 0; i < threadGroups.length; i++) {
            totalThreads += parseInt(threadGroups[i]);
        }
        totalRequests = totalThreads * requestsPerThread;
        totalPayloads = 0;
        if (scope === 'Request') {
            totalPayloads = totalRequests
        } else if (scope === 'Thread') {
            totalPayloads = totalThreads;
        } else if (scope === 'Group') {
            totalPayloads = threadGroups.length
        } else if (scope === 'Benchmark') {
            totalPayloads = 1;
        }

        $("#total-requests").text("" + totalRequests)
        $("#total-payloads").text("" + totalPayloads)
    })
});