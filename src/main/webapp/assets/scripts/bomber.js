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
    $(document).on('focus', 'input.from, #requestsPerThread', function () {
        $(this).select()
    })

    $(document).on('change', 'input.from', function () {
        const $this = $(this)
        const from = $this.val()
        if (from === '' || from < 0)
            return;
        const uid = $("input.uid").val()
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: CONTEXT_PATH + "/api/httpSample/preview?id=" + uid + "&index=" + from,
            success: function (result) {
                $("code.request").html(result.data)
            }
        })
    })

    $(document).on('click', 'button.execute', function () {
        const from = $('input.from').val()
        const to = $('input.to').val()
        const uid = $('input.uid').val()
        if (from !== undefined && to !== undefined && to - from > 0) {
            $.ajax({
                type: "GET",
                contentType: "application/json",
                url: CONTEXT_PATH + "/api/httpSample/execute?id=" + uid + "&index=" + from + "&size=" + (to - from + 1),
                success: function (list) {
                    let errors = []
                    let elapsedTimeInMillis = 0;
                    for (let i = 0; i < list.length; i++) {
                        let data = list[i];
                        if (data.error !== undefined) {
                            errors.push(parseInt(from) + i)
                        } else {
                            elapsedTimeInMillis += parseInt(data.elapsedTimeInMillis);
                        }
                    }
                    if (errors.length > 0) {
                        $("div.errors").removeClass("hidden")
                        $("div.ok").addClass("hidden")
                        let failureReasons = []
                        for (let i = 0; i < Math.min(6, errors.length); i++) {
                            failureReasons.push("[" + errors[i] + "] " + list[errors[0] - parseInt(from)].error)
                        }
                        $("code.errors").html("Errors: " + errors.join(",") + "\n" + failureReasons.join("\n"))
                    } else {
                        $("div.errors").addClass("hidden")
                        $("div.ok").removeClass("hidden")
                        $("code.ok").html("ALL PASSED, 总耗时: " + elapsedTimeInMillis + "ms, 平均: " + (elapsedTimeInMillis/list.length).toFixed(2) + " ms/req")
                    }
                    $("#responses").removeClass("hidden")
                    $("#response").addClass("hidden")
                    $("#error").addClass("hidden")
                }
            })
        } else {
            $.ajax({
                type: "GET",
                contentType: "application/json",
                url: CONTEXT_PATH + "/api/httpSample/execute?id=" + uid + (from === undefined ? "" : "&index=" + from),
                success: function (data) {
                    if (data.content !== undefined) {
                        $("#response").removeClass("hidden")
                        $("code.response").html(data.content)
                        $("span.elapsedTimeInMillis").html(data.elapsedTimeInMillis)
                    } else {
                        $("#response").addClass("hidden")
                    }

                    if (data.error !== undefined) {
                        $("#error").removeClass("hidden")
                        $("code.error").html(data.error)
                    } else {
                        $("#error").addClass("hidden")
                    }

                    $("#responses").addClass("hidden")
                }
            })
        }
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