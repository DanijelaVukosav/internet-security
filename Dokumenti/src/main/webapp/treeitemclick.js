/*
*   This content is licensed according to the W3C Software License at
*   https://www.w3.org/Consortium/Legal/2015/copyright-software-and-document
*
*   File:   Treeitem.js
*
*   Desc:   Setup click events for Tree widget examples
*/

/**
 * ARIA Treeview example
 * @function onload
 * @desc  after page has loaded initialize all treeitems based on the role=treeitem
 */

window.addEventListener('load', function () {

  var treeitems = document.querySelectorAll('.doc'); //[role="treeitem"]

  for (var i = 0; i < treeitems.length; i++) {

    treeitems[i].addEventListener('click', function (event) {
      var treeitem = event.currentTarget;
      var label = treeitem.getAttribute('id');
      if (!label) {
        label = treeitem.innerHTML;
      }

      document.getElementById('selectedFile').innerHTML = label.trim();
	  document.getElementById('download').value = label.trim();
	  document.getElementById('delete').value = label.trim();
	  document.getElementById('moveFile') && (document.getElementById('moveFile').value = label.trim());
	  document.getElementById('oldFileUpdate').value = label.trim();
		console.log("OLD: "+ document.getElementById('oldFileUpdate').value);
      event.stopPropagation();
      event.preventDefault();
    });

  }
	var folders = document.querySelectorAll('.folder'); //[role="treeitem"]
	
	  for (var i = 0; i < folders.length; i++) {
	
	    folders[i].addEventListener('click', function (event) {
	      var folder = event.currentTarget;
	      var label = folder.getAttribute('id');
		  document.getElementById('folderPath').value = label.trim();
		  document.getElementById('moveFolder') && (document.getElementById('moveFolder').value = label.trim());
		  document.getElementById('selectedFolder').innerHTML = label.trim();
		  document.getElementById('createFolderPath') && (document.getElementById('createFolderPath').value = label.trim());
		  document.getElementById('deleteFolder') && (document.getElementById('deleteFolder').value = label.trim());
	      event.stopPropagation();
	      event.preventDefault();
	    });
	
	  }

});