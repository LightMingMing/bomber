$(function () {
    $("#compare").click(function () {
        let checkBoxes = $("tr.selected td.checkbox input")
        let records = []
        for (let i = 0; i < checkBoxes.length; i++) {
            records.push(checkBoxes[i].value)
        }
        console.log("records=" + records)
        window.location.href = "/bombingRecord/compare?recordIds=" + records.join(",")
    });
});