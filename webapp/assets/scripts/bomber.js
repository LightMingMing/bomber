function checkedIds() {
    let arr = []
    $("tr.selected td.checkbox input").each(function (index, item) {
        arr.push(item.value)
    })
    return arr.join(",")
}

function redirectTo(url) {
    window.location.href = url
}