package com.softwhistle.model;

import java.util.ArrayList;
import java.util.List;

public class BadParameterInRequest
{
    public String parameterName;
    public List<String> problems = new ArrayList<String>();

    public BadParameterInRequest parameterName(String parameterName) { this.parameterName = parameterName; return this; }
    public BadParameterInRequest problems(List<String> problems) { this.problems = problems; return this; }
    public BadParameterInRequest addProblem(String problem) { this.problems.add(problem); return this; }
}
