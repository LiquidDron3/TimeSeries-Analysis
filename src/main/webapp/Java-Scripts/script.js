document.addEventListener('DOMContentLoaded', function() {
    toggleVisibilityOfRegressionInputDiv();
    const startButton = document.getElementById('startButton');
    startButton.addEventListener('click', function() {
        updateCanvasBackgroundColors();
    });
});

function toggleVisibilityOfRegressionInputDiv() {
    const inputDivs = document.querySelectorAll('.inputDiv');
    inputDivs.forEach(function(div) {
        const checkbox = div.querySelector('input[type="checkbox"]');
        const hiddenDiv = div.querySelector('.hidden');
        checkbox.addEventListener('change', function() {
            if (this.checked) {
                hiddenDiv.classList.remove('hidden');
            } else {
                hiddenDiv.classList.add('hidden');
            }
        });
        if (!checkbox.checked) {
            hiddenDiv.classList.add('hidden');
        }
    });
}

function updateCanvasBackgroundColors() {
    let regressionSections = document.querySelectorAll('.regression-output-section-class');
    for (let i = 0; i < regressionSections.length; i++) {
        if (i % 2 === 0) {
            regressionSections[i].style.backgroundColor = '#6295A2';
        } else {
            regressionSections[i].style.backgroundColor = '#80B9AD';
        }
    }
}

function handleFileSelect(event) {
    let file = event.target.files[0];
    let reader = new FileReader();
    reader.onload = function(e) {
        document.getElementById("temporary-data-storage").value = e.target.result;
    };
    reader.readAsText(file);
}