package org.dataAnalysis;

import org.teavm.jso.JSBody;

public class D3Handler {

    @JSBody(params = {"selector", "data1", "data2"}, script =
            "selector = selector.trim();" +
            "var container = d3.select(selector);" +

            "var svg = container.select('svg');" +
            "if (!svg.empty()) {" +
            "  svg.remove();" +
            "}" +

            "svg = container.append('svg').attr('width', '80%').attr('height', 700);" +

            "var margin = {top: 20, right: 20, bottom: 30, left: 50};" +
            "var width = container.node().getBoundingClientRect().width * 0.8 - margin.left - margin.right;" +
            "var height = 700 - margin.top - margin.bottom;" +

            "var g = svg.append('g').attr('transform', 'translate(' + margin.left + ',' + margin.top + ')');" +

            "var clip = g.append('defs').append('clipPath').attr('id', 'clip').append('rect').attr('width', width).attr('height', height);" +

            "var x = d3.scaleLinear().range([0, width]);" +
            "var y = d3.scaleLinear().range([height, 0]);" +

            "var line1 = d3.line().x(function(d) { return x(d[1]); }).y(function(d) { return y(d[0]); });" +
            "var line2 = d3.line().x(function(d) { return x(d[1]); }).y(function(d) { return y(d[0]); });" +

            "x.domain(d3.extent(data1.concat(data2), function(d) { return d[1]; }));" +
            "y.domain(d3.extent(data1.concat(data2), function(d) { return d[0]; }));" +

            "g.append('g').attr('class', 'x-axis').attr('transform', 'translate(0,' + height + ')').call(d3.axisBottom(x));" +
            "g.append('g').attr('class', 'y-axis').call(d3.axisLeft(y)).append('text').attr('fill', '#000').attr('transform', 'rotate(-90)').attr('y', 6).attr('dy', '0.71em').attr('text-anchor', 'end').text('Value');" +

            "var path1 = g.append('path').datum(data1).attr('fill', 'none').attr('stroke', 'blue').attr('stroke-linejoin', 'round').attr('stroke-linecap', 'round').attr('stroke-width', 1.5).attr('d', line1).attr('clip-path', 'url(#clip)');" +
            "var path2 = g.append('path').datum(data2).attr('fill', 'none').attr('stroke', 'red').attr('stroke-linejoin', 'round').attr('stroke-linecap', 'round').attr('stroke-width', 1.5).attr('d', line2).attr('clip-path', 'url(#clip)');" +

            "var zoom = d3.zoom().scaleExtent([1, 10]).translateExtent([[0, 0], [width, height]]).extent([[0, 0], [width, height]]).on('zoom', zoomed);" +
            "svg.call(zoom);" +

            "function zoomed(event) {" +
            "  var newX = event.transform.rescaleX(x);" +
            "  var newY = event.transform.rescaleY(y);" +
            "  g.select('.x-axis').call(d3.axisBottom(newX));" +
            "  g.select('.y-axis').call(d3.axisLeft(newY));" +
            "  path1.attr('d', line1.x(function(d) { return newX(d[1]); }).y(function(d) { return newY(d[0]); }));" +
            "  path2.attr('d', line2.x(function(d) { return newX(d[1]); }).y(function(d) { return newY(d[0]); }));" +
            "}" +

            "window.addEventListener('resize', function() {" +
            "  var newWidth = container.node().getBoundingClientRect().width * 0.8 - margin.left - margin.right;" +
            "  svg.attr('width', newWidth + margin.left + margin.right);" +
            "  x.range([0, newWidth]);" +
            "  g.select('.x-axis').call(d3.axisBottom(x));" +
            "  path1.attr('d', line1.x(function(d) { return x(d[1]); }).y(function(d) { return y(d[0]); }));" +
            "  path2.attr('d', line2.x(function(d) { return x(d[1]); }).y(function(d) { return y(d[0]); }));" +
            "});"
    )
    public static native void drawLineGraph(String selector, double[][] data1, double[][] data2);
}