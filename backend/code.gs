function doGet(e) {
  var sheet = SpreadsheetApp.getActiveSpreadsheet().getActiveSheet();
  var data = sheet.getDataRange().getValues();
  var headers = data[0];
  var rows = data.slice(1);
  
  var students = rows.map(function(row) {
    var student = {};
    headers.forEach(function(header, index) {
      student[header] = row[index];
    });
    return student;
  });
  
  return ContentService.createTextOutput(JSON.stringify(students))
    .setMimeType(ContentService.MimeType.JSON);
}

function doPost(e) {
  var sheet = SpreadsheetApp.getActiveSpreadsheet().getActiveSheet();
  var appData = JSON.parse(e.postData.contents);
  var action = appData.action;
  
  var lock = LockService.getScriptLock();
  lock.tryLock(10000);
  
  try {
    if (action === "create") {
      sheet.appendRow([
        appData.nombres,
        appData.apellidos,
        appData.apoderado,
        appData.email,
        appData.telefono,
        appData.nivel,
        appData.grupo,
        appData.dias,
        appData.horario,
        appData.fecha_inicio,
        appData.fecha_fin,
        appData.mensualidad,
        appData.fecha_registro,
        appData.estado
      ]);
      return ContentService.createTextOutput(JSON.stringify({result: "success", message: "Student created"}))
        .setMimeType(ContentService.MimeType.JSON);
    
    } else if (action === "update") {
      var data = sheet.getDataRange().getValues();
      var found = false;
      // Skip header
      for (var i = 1; i < data.length; i++) {
        // Identifier: Nombre + Apellido (assuming uniqueness for this MVP)
        if (data[i][0] == appData.original_nombres && data[i][1] == appData.original_apellidos) {
          // Update row (1-based index)
          var rowIdx = i + 1;
          var range = sheet.getRange(rowIdx, 1, 1, 14);
          range.setValues([[
            appData.nombres,
            appData.apellidos,
            appData.apoderado,
            appData.email,
            appData.telefono,
            appData.nivel,
            appData.grupo,
            appData.dias,
            appData.horario,
            appData.fecha_inicio,
            appData.fecha_fin,
            appData.mensualidad,
            appData.fecha_registro,
            appData.estado
          ]]);
          found = true;
          break;
        }
      }
      
      if (found) {
        return ContentService.createTextOutput(JSON.stringify({result: "success", message: "Student updated"}))
          .setMimeType(ContentService.MimeType.JSON);
      } else {
         return ContentService.createTextOutput(JSON.stringify({result: "error", message: "Student not found"}))
          .setMimeType(ContentService.MimeType.JSON);
      }

    } else if (action === "delete") {
       var data = sheet.getDataRange().getValues();
       var found = false;
       for (var i = 1; i < data.length; i++) {
        if (data[i][0] == appData.nombres && data[i][1] == appData.apellidos) {
          sheet.deleteRow(i + 1);
          found = true;
          break;
        }
       }
       
       if (found) {
         return ContentService.createTextOutput(JSON.stringify({result: "success", message: "Student deleted"}))
           .setMimeType(ContentService.MimeType.JSON);
       } else {
         return ContentService.createTextOutput(JSON.stringify({result: "error", message: "Student not found"}))
           .setMimeType(ContentService.MimeType.JSON);
       }
    }
  } catch (e) {
    return ContentService.createTextOutput(JSON.stringify({result: "error", message: e.toString()}))
      .setMimeType(ContentService.MimeType.JSON);
  } finally {
    lock.releaseLock();
  }
}
