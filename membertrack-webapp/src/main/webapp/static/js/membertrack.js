/* 
 * Copyright (C) 2015 Ilmo Euro
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

"use strict";

-function() {
    var elems = document.querySelectorAll("*[data-destroy]");
    for (var i=0; i<elems.length; i++) {
        -function() {
            var target = elems[i].dataset.destroy;
            if (target === "{parent}") {
                elems[i].addEventListener('click', function() {
                    this.parentNode.parentNode.removeChild(this.parentNode);
                });
            } else {
                elems[i].addEventListener('click', function() {
                    var targets = document.querySelectorAll(target);
                    for (var j=0; j<targets.length; j++) {
                        targets[j].parentNode.removeChild(targets[j]);
                    }
                });
            }
        }();
    }
}();

-function() {
    var elems = document.querySelectorAll("*[data-clone]");
    for (var i=0; i<elems.length; i++) {
        -function() {
            var target = elems[i].dataset.clone;
            elems[i].addEventListener('click', function() {
                var targets = document.querySelectorAll(target);
                for (var j=0; j<targets.length; j++) {
                    var newChild = targets[j].cloneNode(true);
                    newChild.dataset.cloned = "data-cloned";
                    targets[j].parentNode.appendChild(newChild);
                }
            });
        }();
    }
}();