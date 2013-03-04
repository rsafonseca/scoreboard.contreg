<?xml version="1.0" encoding="UTF-8"?>
<!-- This stylesheet converts the Eurostat table of content into RDF
     http://epp.eurostat.ec.europa.eu/NavTree_prod/everybody/BulkDownloadListing?file=table_of_contents.xml

     The ontology file could be placed at: http://epp.eurostat.ec.europa.eu/NavTree_prod/htdocs/ontologies/TableOfContent.owl
     $Id$
  -->
<xsl:stylesheet
        xmlns:nt="urn:eu.europa.ec.eurostat.navtree"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"

        xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
        xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
        xmlns:cr="http://cr.eionet.europa.eu/ontologies/contreg.rdf#"
        xmlns:owl="http://www.w3.org/2002/07/owl#"
        xmlns:dctype="http://purl.org/dc/dcmitype/"
        xmlns:dcterms="http://purl.org/dc/terms/"
        xmlns:void="http://rdfs.org/ns/void#"
        xmlns:skos="http://www.w3.org/2004/02/skos/core#"
        xmlns:cc="http://creativecommons.org/ns#"
        xmlns:foaf="http://xmlns.com/foaf/0.1/"
        xmlns:toc="http://ec.europa.eu/eurostat/ramon/ontologies/toc.rdf#"
        version="1.0" exclude-result-prefixes="nt">

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
    <rdf:RDF xml:base="http://epp.eurostat.ec.europa.eu/NavTree_prod/everybody/BulkDownloadListing?file=table_of_contents.xml">
      <xsl:apply-templates/>
    </rdf:RDF>
  </xsl:template>

  <xsl:template match="nt:tree">
    <foaf:Organization rdf:ID="Eurostat">
      <foaf:name>Eurostat</foaf:name>
      <rdfs:label>Eurostat</rdfs:label>
      <foaf:homepage rdf:resource="http://epp.eurostat.ec.europa.eu/portal/page/portal/eurostat/home/"/>
      <owl:sameAs rdf:resource="http://dbpedia.org/resource/Eurostat"/>
      <!--
      <xsl:for-each select="nt:branch">
        <toc:hasTOC>
          <xsl:attribute name="rdf:resource">#<xsl:value-of select="nt:code"/></xsl:attribute>
        </toc:hasTOC>
      </xsl:for-each>
      -->
    </foaf:Organization>

 <xsl:comment> Declaration of the formats that data is exported in </xsl:comment>
    <dcterms:MediaType rdf:ID="MT-sdmx">
      <rdfs:label>SDMX – Statistical Data and Metadata Exchange</rdfs:label>
      <rdfs:seeAlso rdf:resource="http://sdmx.org/"/>
      <owl:sameAs rdf:resource="http://dbpedia.org/resource/SDMX"/>
    </dcterms:MediaType>
    <dcterms:MediaType rdf:ID="MT-dft">
      <rdfs:label>DFT file</rdfs:label>
      <rdfs:comment>DFT files are intended to efficiently store data organised as multi-dimensional tables.</rdfs:comment>
    </dcterms:MediaType>
    <dcterms:MediaType rdf:ID="MT-tsv">
      <rdfs:label>TSV – Tab-separated values</rdfs:label>
      <rdfs:comment>‘TSV’ files are flat files that include a ‘tab delimited’ sequence of values in each line instead of
      one value per line/record.</rdfs:comment>
    </dcterms:MediaType>

    <skos:ConceptScheme rdf:ID="toc">
      <rdfs:label>Eurostat dataset themes</rdfs:label>
      <dcterms:creator rdf:resource="#Eurostat"/>
      <xsl:for-each select="nt:branch">
        <skos:hasTopConcept>
          <xsl:attribute name="rdf:resource">#<xsl:value-of select="nt:code"/></xsl:attribute>
        </skos:hasTopConcept>
      </xsl:for-each>
    </skos:ConceptScheme>

    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="nt:children">
    <xsl:apply-templates/>
  </xsl:template>

<!--
  <xsl:template match="/nt:tree/nt:branch">
    <skos:ConceptScheme>
      <xsl:attribute name="rdf:about">#<xsl:value-of select="nt:code"/></xsl:attribute>
      <xsl:apply-templates select="nt:title" mode="skos"/>
      <xsl:for-each select="nt:children/nt:branch">
        <skos:hasTopConcept>
          <xsl:attribute name="rdf:resource">#<xsl:value-of select="nt:code"/></xsl:attribute>
        </skos:hasTopConcept>
      </xsl:for-each>
    </skos:ConceptScheme>
    <xsl:apply-templates select="nt:children"/>
  </xsl:template>
-->
  <xsl:template match="nt:branch">
    <skos:Concept>
      <xsl:attribute name="rdf:about">#<xsl:value-of select="nt:code"/></xsl:attribute>
      <xsl:apply-templates select="nt:title" mode="skos"/>
      <skos:inScheme rdf:resource="#toc"/>
      <xsl:for-each select="nt:children/nt:branch">
        <skos:narrower>
          <rdf:Description>
          <xsl:attribute name="rdf:about">#<xsl:value-of select="nt:code"/></xsl:attribute>
          <skos:broader>
          <xsl:attribute name="rdf:resource">#<xsl:value-of select="../../nt:code"/></xsl:attribute>
          </skos:broader>
          </rdf:Description>
        </skos:narrower>
      </xsl:for-each>
      <xsl:for-each select="nt:children/nt:leaf">
        <toc:hasDataset>
          <xsl:attribute name="rdf:resource">#<xsl:value-of select="nt:code"/></xsl:attribute>
        </toc:hasDataset>
      </xsl:for-each>
    </skos:Concept>
    <xsl:apply-templates select="nt:children"/>
  </xsl:template>

<!-- Declaration of a dataset -->
  <xsl:template match="nt:leaf[@type = 'dataset']">
    <void:Dataset>
      <xsl:attribute name="rdf:about">#<xsl:value-of select="nt:code"/></xsl:attribute>
      <dcterms:creator rdf:resource="#Eurostat"/>
      <cc:license rdf:resource="http://creativecommons.org/licenses/by/3.0/lu/"/>
      <cc:morePermissions rdf:resource="http://ec.europa.eu/geninfo/copyright_en.htm"/>
      <dcterms:subject>
        <xsl:attribute name="rdf:resource">#<xsl:value-of select="../../nt:code"/>
        </xsl:attribute>
      </dcterms:subject>
      <toc:hasView><xsl:attribute name="rdf:resource">http://appsso.eurostat.ec.europa.eu/nui/show.do?lang=en&amp;dataset=<xsl:value-of select="nt:code"/></xsl:attribute></toc:hasView>
      <void:dataDump><xsl:attribute name="rdf:resource">http://eurostat.linked-statistics.org/data/<xsl:value-of select="nt:code"/>.rdf</xsl:attribute></void:dataDump>
      <!--
      <void:dataDump><xsl:attribute name="rdf:resource">http://eurostat.linked-statistics.org/dsd/<xsl:value-of select="nt:code"/>.ttl</xsl:attribute></void:dataDump>
      -->
      <xsl:apply-templates/>
      <xsl:call-template name="create-tags"><xsl:with-param name="text" select="nt:code"/></xsl:call-template>
    </void:Dataset>
  </xsl:template>

  <xsl:template match="nt:leaf[@type = 'table']">
    <void:Dataset>
      <xsl:attribute name="rdf:about">#<xsl:value-of select="nt:code"/></xsl:attribute>
      <dcterms:creator rdf:resource="#Eurostat"/>
      <cc:license rdf:resource="http://creativecommons.org/licenses/by/3.0/lu/"/>
      <cc:morePermissions rdf:resource="http://ec.europa.eu/geninfo/copyright_en.htm"/>
      <toc:hasView><xsl:attribute name="rdf:resource">http://epp.eurostat.ec.europa.eu/tgm/table.do?tab=table&amp;plugin=1&amp;language=en&amp;pcode=<xsl:value-of select="nt:code"/></xsl:attribute></toc:hasView>
      <void:dataDump><xsl:attribute name="rdf:resource">http://eurostat.linked-statistics.org/data/<xsl:value-of select="nt:code"/>.rdf</xsl:attribute></void:dataDump>
      <void:dataDump><xsl:attribute name="rdf:resource">http://eurostat.linked-statistics.org/dsd/<xsl:value-of select="nt:code"/>.rdf</xsl:attribute></void:dataDump>
      <xsl:apply-templates/>
    </void:Dataset>
  </xsl:template>

  <xsl:template match="nt:title">
    <xsl:if test="@language = 'en'">
      <rdfs:label><xsl:value-of select="."/></rdfs:label>
      <xsl:call-template name="create-tags-from-title"><xsl:with-param name="text" select="."/></xsl:call-template>
    </xsl:if>
    <dcterms:title>
      <xsl:attribute name="xml:lang">
        <xsl:value-of select="@language"/>
      </xsl:attribute>
      <xsl:value-of select="."/>
    </dcterms:title>
  </xsl:template>

  <xsl:template match="nt:title" mode="skos">
    <xsl:if test="@language = 'en'">
      <rdfs:label><xsl:value-of select="."/></rdfs:label>
    </xsl:if>
    <skos:prefLabel>
      <xsl:attribute name="xml:lang">
        <xsl:value-of select="@language"/>
      </xsl:attribute>
      <xsl:value-of select="."/>
    </skos:prefLabel>
  </xsl:template>

  <xsl:template match="nt:shortDescription">
    <xsl:if test="normalize-space(.) != ''">
      <dcterms:description>
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="@language"/>
        </xsl:attribute>
        <xsl:value-of select="."/>
      </dcterms:description>
    </xsl:if>
  </xsl:template>

  <xsl:template match="nt:lastUpdate">
    <xsl:if test="normalize-space(.) != ''">
      <dcterms:modified>
        <xsl:attribute name="rdf:datatype">http://www.w3.org/2001/XMLSchema#date</xsl:attribute>
        <xsl:value-of select="substring(.,7,4)"/>-<xsl:value-of select="substring(.,4,2)"/>-<xsl:value-of select="substring(.,1,2)"/>
      </dcterms:modified>
    </xsl:if>
  </xsl:template>

  <xsl:template match="nt:downloadLink">
    <toc:downloadLink>  <!-- Could also use dcterms:hasFormat -->
      <dctype:Text>
        <xsl:attribute name="rdf:about">
          <xsl:value-of select="."/>
        </xsl:attribute>
        <rdfs:label>Download file in <xsl:value-of select="@format"/> format</rdfs:label>
        <dcterms:format>
          <xsl:attribute name="rdf:resource">#MT-<xsl:value-of select="@format"/></xsl:attribute>
        </dcterms:format>
      </dctype:Text>
    </toc:downloadLink>
  </xsl:template>

  <xsl:template match="nt:metadata[@format='html']">
    <xsl:if test="normalize-space(.) != ''">
      <toc:metadata>
        <dctype:Text>
          <xsl:attribute name="rdf:about">
            <xsl:value-of select="."/>
          </xsl:attribute>
          <rdfs:label>Metadata as HTML web page</rdfs:label>
        </dctype:Text>
      </toc:metadata>
    </xsl:if>
  </xsl:template>

  <!-- XML elements that have no language code -->
  <xsl:template match="nt:code|nt:dataStart|nt:dataEnd">
    <xsl:if test="normalize-space(.) != ''">
      <xsl:element name="{concat('toc:',local-name())}">
        <xsl:value-of select="."/>
      </xsl:element>
    </xsl:if>
  </xsl:template>

  <xsl:template match="nt:values">
    <xsl:if test="normalize-space(.) != ''">
      <xsl:element name="{concat('toc:',local-name())}">
       <xsl:attribute name="rdf:datatype">http://www.w3.org/2001/XMLSchema#integer</xsl:attribute>
        <xsl:value-of select="."/>
      </xsl:element>
    </xsl:if>
  </xsl:template>

  <!-- XML elements that have language code -->
  <xsl:template match="nt:unit">
    <xsl:if test="normalize-space(.) != ''">
      <xsl:element name="{concat('toc:',local-name())}">
        <xsl:attribute name="xml:lang">
          <xsl:value-of select="@language"/>
        </xsl:attribute>
        <xsl:value-of select="."/>
      </xsl:element>
    </xsl:if>
  </xsl:template>

  <xsl:template match="*"/>

  <xsl:template name="create-tags"><xsl:param name="text" select="."/>
    <xsl:choose>
      <xsl:when test="substring($text,1,5) = 'demo_'"><cr:tag>demographics</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,2) = 'bs'"><cr:tag>business</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,5) = 'bsin_'"><cr:tag>industry</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,2) = 'cp'"><cr:tag>consumer prices</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,5) = 'isen_'"><cr:tag>energy</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,2) = 'na'"><cr:tag>national accounts</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,3) = 'agr'"><cr:tag>agriculture</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,5) = 'cens_'"><cr:tag>census</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,2) = 'ed'"><cr:tag>education</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,5) = 'trng_'"><cr:tag>training</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,2) = 'rd'"><cr:tag>research development</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,5) = 'hlth_'"><cr:tag>health</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,5) = 'tour_'"><cr:tag>tourism</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,5) = 'tran_'"><cr:tag>transport</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,5) = 'road_'"><cr:tag>road freight</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,3) = 'lc_'"><cr:tag>labour cost</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,5) = 'isoc_'"><cr:tag>information society</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,5) = 'migr_'"><cr:tag>migration</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,4) = 'env_'"><cr:tag>environment</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,4) = 'urb_'"><cr:tag>urban audit</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,4) = 'lan_'"><cr:tag>land</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,4) = 'med_'"><cr:tag>mediterranean</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,4) = 'cpc_'"><cr:tag>candidate countries</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,5) = 'enpr_'"><cr:tag>eastern neighbourhood</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,4) = 'gov_'"><cr:tag>government statistics</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,4) = 'ert_'"><cr:tag>exchange rates</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,5) = 'earn_'"><cr:tag>earnings</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,4) = 'lmp_'"><cr:tag>labour market policy</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,4) = 'spr_'"><cr:tag>social protection</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,5) = 'aact_'"><cr:tag>agriculture accounts</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,3) = 'ef_'"><cr:tag>agriculture holdings</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,5) = 'apri_'"><cr:tag>agriculture prices</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,5) = 'apro_'"><cr:tag>agriculture products</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,4) = 'vit_'"><cr:tag>vineyards</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,5) = 'food_'"><cr:tag>food</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,4) = 'for_'"><cr:tag>forestry</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,5) = 'fish_'"><cr:tag>fishery</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,5) = 'rail_'"><cr:tag>railway transport</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,5) = 'pipe_'"><cr:tag>oil pipelines</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,4) = 'mar_'"><cr:tag>maritime transport</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,5) = 'avia_'"><cr:tag>air transport</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,4) = 'nrg_'"><cr:tag>energy</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,6) = 't_env_'"><cr:tag>environment</cr:tag></xsl:when>
      <xsl:when test="substring($text,1,3) = 'sd_'"><cr:tag>sustainable development</cr:tag></xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="create-tags-from-title"><xsl:param name="text" select="."/>
    <xsl:if test="contains($text,'monthly')"><cr:tag>monthly</cr:tag></xsl:if>
    <xsl:if test="contains($text,'Monthly')"><cr:tag>monthly</cr:tag></xsl:if>
    <xsl:if test="contains($text,'quarterly')"><cr:tag>quarterly</cr:tag></xsl:if>
    <xsl:if test="contains($text,'Quarterly')"><cr:tag>quarterly</cr:tag></xsl:if>
    <xsl:if test="contains($text,'annual')"><cr:tag>annual</cr:tag></xsl:if>
    <xsl:if test="contains($text,'Annual')"><cr:tag>annual</cr:tag></xsl:if>
    <xsl:if test="contains($text,'prices')"><cr:tag>prices</cr:tag></xsl:if>
    <xsl:if test="contains($text,'electricity')"><cr:tag>electricity</cr:tag></xsl:if>
    <xsl:if test="contains($text,'wastewater')"><cr:tag>wastewater</cr:tag></xsl:if>
    <xsl:if test="contains($text,'NUTS level 2')"><cr:tag>nuts2</cr:tag></xsl:if>
    <xsl:if test="contains($text,'NUTS 2')"><cr:tag>nuts2</cr:tag></xsl:if>
    <xsl:if test="contains($text,'NUTS2')"><cr:tag>nuts2</cr:tag></xsl:if>
    <xsl:if test="contains($text,'NUTS level 3')"><cr:tag>nuts3</cr:tag></xsl:if>
    <xsl:if test="contains($text,'NUTS 3')"><cr:tag>nuts3</cr:tag></xsl:if>
    <xsl:if test="contains($text,'NUTS3')"><cr:tag>nuts3</cr:tag></xsl:if>
  </xsl:template>
</xsl:stylesheet>
