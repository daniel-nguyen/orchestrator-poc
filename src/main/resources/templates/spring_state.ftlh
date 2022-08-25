<!DOCTYPE HTML>
<head>
    <title>Orchestrator POC</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap" rel="stylesheet">
</head>
<body>
<br>
<div>
    <form action="/create_machine">
        <table style="background-color: whitesmoke">
            <tr>
                <th style="background-color: whitesmoke">Machine type:</th>
                <td>
                    <select name="machine_type">
                        <#list stateMachineTypes as machineType>
                            <option value="${machineType.id}">${machineType.name}</option>
                        </#list>
                    </select>
                </td>
            </tr>
            <tr>
                <th style="background-color: whitesmoke">Machine name:</th>
                <td>
                    <input type="text" name="machine_name"/>
                </td>
            </tr>
            <tr>
                <th colspan="2" style="background-color: whitesmoke">
                    <input type="submit" value="Create new machine">
                </th>
            </tr>
        </table>
    </form>
</div>
<div class="mytabs">
    <#list stateMachines as machine>
    <input type="radio" id="tab${machine.id}" name="mytabs" <#if machine?is_first>checked="checked"</#if></div>
<label for="tab${machine.id}">${machine.id}</label>
<div class="tab">
    <h2>${machine.id}</h2>
    <div class="container">
        <div class="column-left">
            <p>
                Current state: <b>${machine.state}</b>
            </p>
            <#list machineIdToAvailableEvents[machine.id?c]>
                <form action="/send_event">
                    <input type="hidden" name="machine_id" value="${machine.id}">
                    <#items as event>
                        <input class="normal_radio" type="radio" id="event_${event?counter}" name="event" value="${event.name()}" <#if event?is_first>checked="checked"</#if>>
                        <label class="normal_label" for="event_${event?counter}">${event.name()}</label>
                        <br>
                    </#items>
                    <br>
                    <input type="submit" value="Send event">
                </form>
            <#else>
                <p>
                    There is no acceptable events from current state
                </p>
            </#list>
            <br>
            Context:
            <br>
<#--
            <#list machine.context.attributes?keys?sort>
                <table>
                    <thead>
                    <tr>
                        <th>Key</th>
                        <th>Value</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#items as key>
                        <tr>
                            <td>${key}</td>
                            <td>${machine.context.attributes[key]}</td>
                        </tr>
                    </#items>
                    </tbody>
                </table>
            <#else>
                <p>
                    Context is empty
                </p>
            </#list>
-->
        </div>
        <div class="column-right">
            <img src="/svg/${machine.id}?markState=${machine.state}"/>
        </div>
    </div>
</div>
</#list>
</div>
</body>
</html>

<style>
    table, th, td {
        border: 1px solid black;
        border-collapse: collapse;
    }

    th {
        background: #ccc;
    }

    th, td {
        padding: 5px;
    }

    .normal_radio {
        display: inline !important;
    }

    .normal_label {
        font-weight: normal !important;
        padding: 0 !important;
        background: none !important;
    }

    body {
        background: #ccc;
        font-family: 'Roboto', sans-serif;
    }

    .container {
        padding: 20px;
    }

    .column-left {
        width: available;
        float: left;
    }

    .column-right {
        width: min-content;
        float: right;
    }

    .mytabs {
        display: flex;
        flex-wrap: wrap;
        max-width: 100%;
        margin: 50px auto;
        padding: 25px;
    }

    .mytabs input[type="radio"] {
        display: none;
    }

    .mytabs label {
        padding: 25px;
        background: #e2e2e2;
        font-weight: bold;
    }

    .mytabs .tab {
        width: 100%;
        padding: 20px;
        background: #fff;
        order: 1;
        display: none;
    }

    .mytabs .tab h2 {
        font-size: 3em;
    }

    .mytabs input[type='radio']:checked + label + .tab {
        display: block;
    }

    .mytabs input[type="radio"]:checked + label {
        background: #fff;
    }
</style>